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

@app.get("/ask")
async def ask_model(req:Request ,res:Response):
    prompt = req.query_params.get("prompt", "")

    if len(prompt) == 0:
        raise HTTPException(400, "needs query parameter 'prompt'")

    request = PromptRequest(prompt=prompt.strip())

    message= await get_response(request)

    return {"response": message}

    async def message_stream():
        for word in message:
            yield word.encode("utf-8")

    return StreamingResponse(message_stream(), media_type="text/plain")


# TODO TEMP REMOVE
@app.get('/debug/tools')
def list_tools():
    return TOOLS