import json
import os
from datetime import datetime
from random import random
from pydantic import BaseModel
from fastapi import HTTPException

from tools import TOOL_MAP, TOOLS, run_tool

from openai import OpenAI


VLLM_URL = os.getenv("VLLM_URL", "http://localhost:8000") + '/v1'
MODEL = os.getenv("MODEL_NAME")


client = OpenAI(base_url=VLLM_URL, api_key="")

class PromptRequest(BaseModel):
    prompt: str
    max_tokens: int = 1000
    temperature: float = 0.7

class PromptResponse(BaseModel):
    model: str
    response: str


async def get_response(req: PromptRequest) -> str:
    """
    Sends a prompt to the vLLM model and returns the generated response.
    """
    global client

    messages = [
        {"role": "system", "content": "You are a helpful assistant that answers questions based on files uploaded by the user. You must retreave the context of the file before answering any questions to user! Use tools that help you retriev the file context.\n\nIf the tools give you irrelavent information tell the user there is no information on based on the request. If the tool returns the same answer, do not repeatedly call the same tool (max 3 times if you must), it as irrelavent information and notify the user of this."},
        {"role": "user", "content": req.prompt}
    ]
    while True:
        res = client.chat.completions.create(
            model=MODEL,
            messages=messages,
            tools=TOOLS,
            max_tokens=req.max_tokens,
            temperature=req.temperature,
        )

        choice = res.choices[0]

        if choice.finish_reason != "tool_calls":
            return choice.message.content
        
        messages.append(choice.message)

        for tool_call in choice.message.tool_calls:
            print("[get_respnse] Calling tool:", tool_call.function.name)

            endpoint = TOOL_MAP.get(tool_call.function.name, '')
            arguments = json.loads(tool_call.function.arguments)

            try:
                result = run_tool(endpoint, arguments) if endpoint else f"Unknown tool: {tool_call.function.name}"
            except Exception as e:
                result = "Error calling tool."
                print("[get_respnse]: Tool calling error:\n\t", e)
            
            messages.append({
                "role": "tool",
                "tool_call_id": tool_call.id,
                "content": str(result),
            })