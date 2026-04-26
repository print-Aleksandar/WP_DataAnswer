import asyncio

from fastapi import FastAPI, HTTPException, Response, Request
from fastapi.responses import StreamingResponse

from vllmUtils import get_response, PromptRequest, PromptResponse

app = FastAPI()

@app.get("/ask")
async def ask_model(req:Request ,res:Response):
    prompt = req.query_params.get("prompt", "")

    if len(prompt) == 0:
        raise HTTPException(400, "needs query paramater 'prompt'")

    request = PromptRequest(prompt=prompt.strip())

    message= get_response(request)

    return {"response": message}

    async def message_stream():
        for word in message:
            yield word.encode("utf-8")

    return StreamingResponse(message_stream(), media_type="text/plain")