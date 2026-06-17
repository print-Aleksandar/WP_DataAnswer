import asyncio

from fastapi import FastAPI, HTTPException, Response, Request
from fastapi.responses import StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from vllmUtils import get_response, PromptRequest, PromptResponse, init_vllm
from tools import TOOLS, init_tools

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@asynccontextmanager
async def lifespan(app: FastAPI):
    await init_tools()
    await init_vllm()
    yield

    # cleanup

app = FastAPI(lifespan=lifespan)

@app.post("/ask", response_class=StreamingResponse)
async def ask_model(req:PromptRequest):
    return StreamingResponse(
        get_response(req), 
        media_type="text/event-stream"
    )