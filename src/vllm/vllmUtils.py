import asyncio
import json
import os
from datetime import datetime
from random import random
from pydantic import BaseModel
from fastapi import HTTPException

from tools import TOOL_MAP, TOOLS, run_tool

from openai import AsyncOpenAI


VLLM_URL = os.getenv("VLLM_URL", "http://localhost:8000") + '/v1'
MODEL = ''
SYSTEM_PROMPT = "You are a helpful assistant that answers questions based on files uploaded by the user. You must retreave the context of the file before answering any questions to user! Use tools that help you retriev the file context.\n\nIf the tools give you irrelavent information tell the user there is no information on based on the request. If the tool returns the same answer, do not repeatedly call the same tool (max 3 times if you must), it as irrelavent information and notify the user of this."

client = AsyncOpenAI(base_url=VLLM_URL, api_key="DUMMY_KEY")

class PromptRequest(BaseModel):
    user_id: int
    chat_id: int
    prompt: str
    history: list[dict] = []
    max_tokens: int = 1024
    temperature: float = 0.7

class PromptResponse(BaseModel):
    model: str
    response: str

async def init_vllm():
    global MODEL, client

    model_list = await client.models.list()
    print(model_list)
    MODEL = model_list.data[0].id

    print('[init_vllm]: fetched model', MODEL)



async def get_response(req: PromptRequest):
    """
    Sends a prompt to the vLLM model and returns the generated response.
    """
    global client

    messages = [
        {"role": "system", "content": SYSTEM_PROMPT},
        *req.history,
        {"role": "user", "content": req.prompt}
    ]
    while True:
        stream = await client.chat.completions.create(
            model=MODEL,
            messages=messages,
            tools=TOOLS,
            max_tokens=req.max_tokens,
            temperature=req.temperature,
            stream=True,
        )

        tool_calls_acc = {}
        assistant_content = ""
        finish_reason = None

        async for chunk in stream:
            delta = chunk.choices[0].delta
            finish_reason = chunk.choices[0].finish_reason or finish_reason


            if delta.content:
                assistant_content += delta.content
                yield delta.content

            if delta.tool_calls:
                for tc in delta.tool_calls:
                    acc = tool_calls_acc.setdefault(tc.index, {"id": tc.id, "name": "", "arguments": ""})
                    if tc.function.name:
                        acc["name"] += tc.function.name
                    if tc.function.arguments:
                        acc["arguments"] += tc.function.arguments
        
        if finish_reason != "tool_calls":
            return

        tool_calls_list = [
            {"id": tc["id"], "type": "function",
             "function": {"name": tc["name"], "arguments": tc["arguments"]}}
            for tc in tool_calls_acc.values()
        ]

        messages.append({"role": "assistant", "content": assistant_content or None, "tool_calls": tool_calls_list})

        for tc in tool_calls_list:
            endpoint = TOOL_MAP.get(tc["function"]["name"], "")
            arguments = json.loads(tc["function"]["arguments"])
            try:
                result = run_tool(endpoint, arguments, file_id=req.file_id, user_id=req.user_id, chat_id=req.chat_id) \
                if endpoint else \
                f"Unknown tool: {tc['function']['name']}"
            except Exception as e:
                result = "Error calling tool."
                print("[get_respnse]: Tool calling error:\n\t", e)
            
            messages.append({
                "role": "tool",
                "tool_call_id": tc["id"], 
                "content": str(result),
            })