import faiss
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from langchain_text_splitters import RecursiveCharacterTextSplitter
import numpy as np
from fastapi.concurrency import run_in_threadpool
from Requests.upload_request import UploadRequest
from Requests.ask_request import AskRequest
from file_utils import get_supported_types, parse_file
from index_utils import load_index, save_index, get_user_lock, get_user_path, MODEL

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.get("/upload")
def supported_types():
    return get_supported_types()

@app.post("/upload")
async def upload_file(request:UploadRequest):

    #if user_id is null throw error
    if request.user_id is None:
        raise HTTPException(status_code=400, detail="user_id cannot be empty")

    #proverkata dali e pdf e proverena vo java delot

    #getting the text from the document
    file_content = await request.file.read()

    def extract_and_embed():
        # Extract text from file
        text = parse_file(request.file, file_content)

        # splitting into chunks
        text_spliter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)

        chunks = text_spliter.split_text(text)

        if not chunks:
            raise ValueError("No text chunks produced.")

        # embedding chunks
        embeddings = MODEL.encode(chunks, show_progress_bar=False, batch_size=32)
        embeddings = np.array(embeddings).astype("float32")
        faiss.normalize_L2(embeddings)

        return chunks, embeddings

    try:
        chunks, embeddings = await run_in_threadpool(extract_and_embed)
    except Exception as e:
        raise HTTPException(status_code=422, detail=str(e))

    def index_and_save():
        user_path = get_user_path(request.user_id, request.chat_id)
        user_path.mkdir(parents=True, exist_ok=True)

        index, existing_chunks = load_index(user_path)

        if index is None:
            # index = faiss.IndexFlatL2(embeddings.shape[1])
            index = faiss.IndexFlatIP(embeddings.shape[1])

        index.add(embeddings)
        existing_chunks.extend(chunks)

        save_index(index, existing_chunks, user_path)
        return len(chunks)

    async with get_user_lock(request.user_id):  # one upload at a time per user
        chunk_count = await run_in_threadpool(index_and_save)

    return {
        "filename": request.file.filename,
        "chunks_indexed": chunk_count,
    }

#=================================

@app.get("/tools")
async def get_tools():

    return [
            {
                "name": "get_file_context",
                "description": "Returns the relevant chunks from the uploaded file of the chat based on the users question",
                "parameters": {
                    "type": "object",
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
    user_path = get_user_path(request.user_id, request.chat_id)

    # Load index
    index, existing_chunks = await run_in_threadpool(load_index, user_path)

    if index is None or not existing_chunks:
        raise HTTPException(
            status_code=404,
            detail=f"No documents indexed for user {request.user_id}. Upload a file first.",
        )

    # Embed the question (blocking → threadpool)
    def embed_question():
        emb_q = MODEL.encode([request.question], show_progress_bar=False)
        np.array(emb_q, dtype="float32")
        faiss.normalize_L2(emb_q)
        return emb_q

    embedded_question = await run_in_threadpool(embed_question)

    # FAISS search
    def search():
        k = min(request.top_k, len(existing_chunks))
        distances, indices = index.search(embedded_question, k)
        return [
            existing_chunks[i]
            for i, d in zip(indices[0], distances[0])
            if 0 <= i < len(existing_chunks) and d > 0.3
        ]

    retrieved_chunks = await run_in_threadpool(search)

    return "\n\n---\n\n".join(retrieved_chunks)
