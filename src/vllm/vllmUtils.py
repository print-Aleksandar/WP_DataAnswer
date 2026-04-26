import os
from datetime import datetime
from random import random
from pydantic import BaseModel
from fastapi import HTTPException

from openai import OpenAI


VLLM_URL = os.getenv("VLLM_URL", "http://localhost:8000") + '/v1'
MODEL = os.getenv("MODEL_NAME")


client = OpenAI(base_url=VLLM_URL, api_key="")

class PromptRequest(BaseModel):
    prompt: str
    max_tokens: int = 200
    temperature: float = 0.7

class PromptResponse(BaseModel):
    model: str
    response: str

# TODO Temporary tools should get replaced with real ones
def get_current_time():
    """
    This tool returns the user's local time in 24h format. 
    """
    return datetime.now().strftime("%H:%M:%S")

def get_local_weather_forcast():
    """
    Returns the local weather forcast of the user's location.
    """
    return "sunny" if random() > 0.4 else "rain"

TOOLS = [
    {
        "type": "function",
        "function": {
            "name": fn.__name__,
            "description": fn.__doc__,
            "parameters": {
                "type": "object",
                "properties": {},
                "required": []
            }
        }
    } for fn in (get_current_time, get_local_weather_forcast)
]

TOOL_MAP = { fn.__name__ : fn for fn in (get_current_time, get_local_weather_forcast) }
# ~~~

def get_response(req: PromptRequest) -> str:
    """
    Sends a prompt to the vLLM model and returns the generated response.
    """
    global client

    messages = [
        {"role": "system", "content": "You are a helpful assistant."},
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
            fn = TOOL_MAP.get(tool_call.function.name)
            result = fn() if fn else f"Unknown tool: {tool_call.function.name}"
            
            print("Calling tool:", tool_call.function.name)

            messages.append({
                "role": "tool",
                "tool_call_id": tool_call.id,
                "content": str(result),
            })