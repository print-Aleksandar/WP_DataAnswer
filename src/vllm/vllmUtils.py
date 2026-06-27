import asyncio
import json
import os
from datetime import datetime
from random import random
from pydantic import BaseModel
from fastapi import HTTPException

from tools import TOOL_MAP, TOOLS, run_tool

from openai import AsyncOpenAI
import tiktoken


LLM_URL = os.getenv("LLM_URL")
MODEL = os.getenv("MODEL_NAME")
API_KEY = os.getenv("LLM_KEY")
SYSTEM_PROMPT = "You are a helpful assistant that answers questions based on files uploaded by the user. You must retreave the context of the file before answering any questions to user! Use tools that help you retriev the file context.\n\nIf the tools give you irrelavent information tell the user there is no information on based on the request. If the tool returns the same answer, do not repeatedly call the same tool (max 3 times if you must), it as irrelavent information and notify the user of this."

print(LLM_URL, MODEL, API_KEY)

client = AsyncOpenAI(base_url=LLM_URL, api_key=API_KEY)

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

class ChatGenerationRequest(BaseModel):
    user_message: str
    assistant_response: str

async def init_vllm():
    global MODEL, client

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
            if len(chunk.choices) == 0: 
                print("[get_response]: SKIPPED CHUNK - empty choices")
                continue
            delta = chunk.choices[0].delta
            finish_reason = chunk.choices[0].finish_reason or finish_reason


            if delta.content:
                assistant_content += delta.content
                yield json.dumps({"token": delta.content})

            if delta.tool_calls:
                for tc in delta.tool_calls:
                    acc = tool_calls_acc.setdefault(tc.index, {"id": tc.id, "name": "", "arguments": ""})

                    if tc.function.name:
                        acc["name"] += tc.function.name

                        # emit tool calling if it happens
                        if not acc["name"]:
                            pass

                    if tc.function.arguments:
                        acc["arguments"] += tc.function.arguments

        messages.append({"role": "assistant", "content": assistant_content or None})


        if finish_reason != "tool_calls":
            break

        tool_calls_list = [
            {"id": tc["id"], "type": "function",
             "function": {"name": tc["name"], "arguments": tc["arguments"]}}
            for tc in tool_calls_acc.values()
        ]

        messages[-1]["tool_calls"] = tool_calls_list


        for tc in tool_calls_list:
            endpoint = TOOL_MAP.get(tc["function"]["name"], "")
            arguments = json.loads(tc["function"]["arguments"])

            # notify calling of tool
            yield json.dumps({"tool_call": tc["function"]["name"], "tool_id": tc["id"]}) 
            
            try:
                result = run_tool(endpoint, arguments, user_id=req.user_id, chat_id=req.chat_id) \
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

            # notify tool response
            yield json.dumps({"tool_response": str(result), "tool_id": tc["id"]})

    print(messages[len(req.history)+1:])
    prompt_tokens_est = sum( count_tokens(m["content"], MODEL) for m in messages[len(req.history)+1:] )

    yield json.dumps({"token_usage": prompt_tokens_est})


def count_tokens(text: str, model_hint: str = "gpt-4o") -> int:
    if not text:
        return 0
    
    try:
        enc = tiktoken.encoding_for_model(model_hint)
    except KeyError:
        enc = tiktoken.get_encoding("o200k_base")  # reasonable fallback

    return len(enc.encode(text))


async def generate_chat_title(user_message: str, assistant_response: str) -> str:
    response = await client.chat.completions.create(
        model=MODEL,
        temperature=0.2,
        max_tokens=20,
        messages=[
            {
                "role": "system",
                "content": (
                    "You are a title generator for chat conversations."
                    "Rules:"
                    "- 3 to 6 words"
                    "- no punctuation"
                    "- no quotes"
                    "- title case or natural case"
                    "- return only the title"
                ),
            },
            {
                "role": "user",
                "content": f"""
                User:
                {user_message}

                Assistant:
                {assistant_response}"""
            }
        ]
    )

    return response.choices[0].message.content.strip()