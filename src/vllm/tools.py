import json
import time

import httpx
import warnings
import os

TOOLS = []
TOOL_MAP = dict()

SERVICE_URLS = os.getenv("TOOL_SERVICE_URLS", "").split(',')

async def init_tools():
    global TOOLS, TOOL_MAP

    for url in SERVICE_URLS:
        data:list[dict] = []
        try:
            # get tools from service
            async with httpx.AsyncClient(timeout=30.0) as client:
                response = await client.get(url + '/tools')
                response.raise_for_status()
                data:list[dict] = response.json()
        except Exception as e:
            warnings.warn(f"Getting tools at '{url}' failed:\n{e}", RuntimeWarning)
        
        
        # add tools for use
        for tool in data:
            # skip tools with same name
            if tool["name"] in TOOL_MAP:
                warnings.warn(f"'{tool['name']}' already registered with '{TOOL_MAP[tool['name']]}' - skipping", UserWarning)
                continue
            try:
                TOOL_MAP[tool['name']] = url + tool['endpoint']
                TOOLS.append({
                    "type": "function",
                    "function": {
                        "name": tool["name"],
                        "description": tool["description"],
                        "parameters": tool["parameters"]
                    }
                }) 
            except Exception as e:
                warnings.warn(f"Formatting tool '{tool['name']}' at '{url}' failed:\n\t{e}")

    print("[init_tool] Colecteded tools:", TOOL_MAP)

def run_tool(url, params:dict):
        print('run_tool:', params, type(params))
        
        payload = params.copy()

        payload['user_id'] = 1 # TODO Temp for testing - remove!
        payload['chat_id'] = 1 # TODO Temp for testing - remove!
        response = httpx.post(url, json=payload)
        response.raise_for_status()
        return response.text