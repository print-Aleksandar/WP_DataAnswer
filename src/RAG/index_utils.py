import io

from sentence_transformers import SentenceTransformer
import faiss
import asyncio
import tempfile
import pickle
import os
from pathlib import Path

from minio_utils import MinioClient


MODEL = SentenceTransformer("all-MiniLM-L6-v2")

MINIO_CLIENT = MinioClient()

_user_locks: dict[int, asyncio.Lock] = {}

def save_index(index: faiss.Index, chunks, path: Path):
    # Create object paths
    write_path = str("rag" / path)
    
    # Get index buffer
    index_buffer = io.BytesIO()

    with tempfile.NamedTemporaryFile(delete=False) as tmp_index:
        faiss.write_index(index, tmp_index.name)
        with open(tmp_index.name, "rb") as f:
            index_buffer.write(f.read())

        os.unlink(tmp_index.name)

    index_buffer.seek(0)
    
    # get chunks buffer
    chunks_buffer = io.BytesIO()
    pickle.dump(chunks, chunks_buffer)
    chunks_buffer.seek(0)
    
    MINIO_CLIENT.write_entry(index_buffer, chunks_buffer, write_path)
    
def load_index(user_path : Path):
    # Create object paths
    read_path = f"rag/{user_path}"
    
    existing_chunks = []
    index = None
    
    try:
        # get index and chunks from minio
        index_response, chunk_response = MINIO_CLIENT.read_entry(read_path)

        # load index
        with tempfile.NamedTemporaryFile(delete=False) as tmp_index:
            for data in index_response.stream(1024 * 1024):
                tmp_index.write(data)
            tmp_index_path = tmp_index.name
        
        index = faiss.read_index(tmp_index_path)
        os.unlink(tmp_index_path)
        
        # load chunks
        chunks_buffer = io.BytesIO()
        for data in chunk_response.stream(1024 * 1024):
            chunks_buffer.write(data)
        chunks_buffer.seek(0)
        existing_chunks = pickle.load(chunks_buffer)
        
    except Exception:
        # If objects don't exist in MinIO, return empty values
        pass
    
    return index, existing_chunks


def get_user_path(user_id : int, chat_id: int):
    return Path(f"storage/{user_id}/{chat_id}")

def get_user_lock(user_id: int) -> asyncio.Lock:
    if user_id not in _user_locks:
        _user_locks[user_id] = asyncio.Lock()
    return _user_locks[user_id]