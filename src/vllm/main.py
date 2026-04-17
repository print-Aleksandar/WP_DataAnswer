import asyncio

from fastapi import FastAPI, HTTPException, Response
from fastapi.responses import StreamingResponse

app = FastAPI()

@app.get("/ask")
async def ask_model(res: Response):
    
    # Test function for streaming response
    message= ["Not ","yet ","implemented! ","sry"]

    async def message_stream():
        for word in message:
            yield word.encode("utf-8")

    return StreamingResponse(message_stream(), media_type="text/plain")