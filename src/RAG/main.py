import asyncio
import faiss
import fitz
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from langchain_text_splitters import RecursiveCharacterTextSplitter
from sentence_transformers import SentenceTransformer
from pathlib import Path
import numpy as np
import pickle
from fastapi.concurrency import run_in_threadpool
from Requests.ask_request import AskRequest

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

model = SentenceTransformer("all-MiniLM-L6-v2")

_user_locks: dict[int, asyncio.Lock] = {}

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.get("/health")
async def health():
    return {"status": "ok"}

#=====================================================================================
def get_user_lock(user_id: int) -> asyncio.Lock:
    if user_id not in _user_locks:
        _user_locks[user_id] = asyncio.Lock()
    return _user_locks[user_id]


def save_index( index: faiss.Index, chunks, path: Path):
    faiss.write_index(index, str(path / "index.faiss"))

    with open(path / "chunks.pkl", "wb") as f:
        pickle.dump(chunks, f)

def load_index(user_path : Path):
    index_path = user_path / "index.faiss"
    chunks_path = user_path / "chunks.pkl"
    existing_chunks = []
    index = None
    if index_path.exists() and chunks_path.exists():
        index = faiss.read_index(str(index_path))
        with open(chunks_path, "rb") as f:
            existing_chunks = pickle.load(f)

    return index, existing_chunks


def get_user_path(user_id : int):
    return Path(f"storage/{user_id}")

#===============================================================================================

@app.post("/upload")
async def upload_pdf(user_id: int, file: UploadFile = File(...)):

    #if user_id is null throw error
    if user_id is None:
        raise HTTPException(status_code=400, detail="user_id cannot be empty")

    #proverkata dali e pdf e proverena vo java delot

    #getting the text from the document
    file_content = await file.read()

    def extract_and_embed():
        with fitz.open(stream=file_content, filetype="pdf") as pdf:
            text = ""
            for page in pdf:
                text += page.get_text()

        if not text.strip():
            raise ValueError("Could not extract text from PDF.")

        # splitting into chunks
        text_spliter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)

        chunks = text_spliter.split_text(text)

        if not chunks:
            raise ValueError("No text chunks produced.")

        # embedding chunks
        embeddings = model.encode(chunks, show_progress_bar=False, batch_size=32)
        embeddings = np.array(embeddings).astype("float32")

        return chunks, embeddings

    try:
        chunks, embeddings = await run_in_threadpool(extract_and_embed)
    except ValueError as e:
        raise HTTPException(status_code=422, detail=str(e))

    def index_and_save():
        user_path = get_user_path(user_id)
        user_path.mkdir(parents=True, exist_ok=True)

        index, existing_chunks = load_index(user_path)

        if index is None:
            index = faiss.IndexFlatL2(embeddings.shape[1])

        index.add(embeddings)
        existing_chunks.extend(chunks)

        save_index(index, existing_chunks, user_path)
        return len(chunks)

    async with get_user_lock(user_id):  # one upload at a time per user
        chunk_count = await run_in_threadpool(index_and_save)

    return {
        "filename": file.filename,
        "chunks_indexed": chunk_count,
    }


# RAG_SYSTEM_PROMPT = """You are a helpful assistant that answers questions based strictly on the provided context.
# If the answer is not found in the context, say "I don't have enough information in the provided documents to answer this question."
# Be concise and accurate."""

#=================================
@app.get("/tools")
async def get_tools():

    return [
            {
                "name": "get_file_context",
                "description": "Returns the relevant chunks from the uploaded file of the chat based on the users question",
                "parameters": {
                    "properties": {
                        'question': {
                            'type': 'string',
                            'description': 'The user\'s question to search for in the file'
                        }
                    },
                    "required": ["question"]
                },
                "endpoint": "/chunks"
            },
           ]

@app.post('/chunks')
async def get_chunks(request: AskRequest):
    user_path = get_user_path(request.user_id)

    # Load index
    index, existing_chunks = await run_in_threadpool(load_index, user_path)

    if index is None or not existing_chunks:
        raise HTTPException(
            status_code=404,
            detail=f"No documents indexed for user {request.user_id}. Upload a file first.",
        )

    # Embed the question (blocking → threadpool)
    def embed_question():
        emb_q = model.encode([request.question], show_progress_bar=False)
        return np.array(emb_q, dtype="float32")

    embedded_question = await run_in_threadpool(embed_question)

    # FAISS search
    def search():
        k = min(request.top_k, len(existing_chunks))
        distances, indices = index.search(embedded_question, k)
        return [
            existing_chunks[i]
            for i, d in zip(indices[0], distances[0])
            if 0 <= i < len(existing_chunks) and d > 0.5
        ]

    retrieved_chunks = await run_in_threadpool(search)

    return "\n\n---\n\n".join(retrieved_chunks)
