import json
import pandas as pd
from fastapi import FastAPI, HTTPException, UploadFile, File
from contextlib import asynccontextmanager
from storage import DB_Storage
from pydantic import BaseModel
from typing import List, Any, Optional, Tuple
from static import AGG_MAP, BOOLEAN_TRANSFORMS, NUMERIC_PREDICATES, NUMERIC_TRANSFORMS, TEXT_PREDICATES, TEXT_TRANSFORMS, VALUE_NEEDED_TRANSFORMS
from agg_cl import Aggregator

app = FastAPI()

class AggregationRequest(BaseModel):
    user_id: int
    chat_id: int
    aggregate_function: str
    target_column: str
    filters: Optional[List[Tuple[str, str, Any]]] = None
    grouping_columns: Optional[List[str]] = None
    target_criteria_predicate: Optional[str] = None
    target_criteria_value: Optional[Any] = None
    transforms: Optional[List[Tuple[str, str, Any]]] = None
    exclude_cols: Optional[List[str]] = None

class ColumnsRequest(BaseModel):
    user_id: int
    chat_id: int
    json_data: Optional[List[dict]] = None

@app.post("/aggregate")
async def run_aggregator(request: AggregationRequest):
    try:
        db = DB_Storage()

        agg = Aggregator(
            json_data=db.get_data(request.user_id, request.chat_id),
            aggregate_function=request.aggregate_function,
            target_column=request.target_column,
            filters=request.filters,
            target_criteria_predicate=request.target_criteria_predicate,
            target_criteria_value=request.target_criteria_value,
            grouping_columns=request.grouping_columns,
            transforms=request.transforms,
            exclude_cols=request.exclude_cols
        )

        db.close()

        result = agg.get_result()
        return result

    except Exception as e:
        print(e)
        raise HTTPException(status_code=400, detail=str(e))

@app.post('/columns')
async def get_columns_from_file(req:ColumnsRequest):
    if req.json_data:
        df = pd.DataFrame(req.json_data)
    else:
        db = DB_Storage()
        df = pd.DataFrame(db.get_data(req.user_id, req.chat_id))
        db.close()
    
    return [ [col, dtype.name] for col, dtype in df.dtypes.items()]

@app.get('/tools')
def get_tools():
    return [
            {
                "name": "aggregate_file",
                "description": "Aggregate, transform, filter and manipulate tabular or structured data (example spreadsheet, csv, json, etc.).",
                "parameters": {
                    "type": 'object',
                    "properties": {
                        'aggregate_function': {
                            'type': 'string',
                            'description': f"The aggregate function performed on a column. This parameter expect a value from [{', '.join(AGG_MAP.keys())}]."
                        },
                        'target_column': {
                            'type': 'string',
                            'description': "Name of the column to apply 'aggregate_function' to."
                        },
                        'filters': {
                            'type': 'array',
                            'items': {
                                "type": "array",
                                "prefixItems": [{ "type": "string" }, { "type": "string" }, {}],
                                "minItems": 3,
                                "maxItems": 3
                            },
                            'description': f"Optional filters for columns. Each filter is a list of [column_name, predicate, value].\nPredicate values for numeric values must be from [{', '.join(NUMERIC_PREDICATES)}].\nPredicate values for text must be from [{', '.join(TEXT_PREDICATES)}]."
                        },
                        'grouping_columns': {
                            "type": "array", 
                            "items": { "type": "string" },
                            'description': "Groups the specified columns then 'aggregate_function' is applied to the target column within each group, and the result includes the grouping columns plus the aggregated values."
                        },
                        'transforms': {
                            'type': 'array',
                            'items': {
                                "type": "array",
                                "prefixItems": [{ "type": "string" }, { "type": "string" }, {}],
                                "minItems": 2,
                                "maxItems": 3
                            },
                            'description': f"Perform transformations on columns. The parameter expects a list of [column_name, transofrmation, value].\nTransofrmation functions for numeric values must be from [{', '.join(NUMERIC_TRANSFORMS)}].\nTransformation functions for text must be from [{', '.join(TEXT_TRANSFORMS)}].\nTransformation functions for boolean values must be from [{', '.join(BOOLEAN_TRANSFORMS)}].\nIf a transformation function is in [{', '.join(VALUE_NEEDED_TRANSFORMS)}] the third element 'value' must be provided, otherwise it is not required."
                        },
                        'exclude_cols': {
                            "type": "array", 
                            "items": { "type": "string" },
                            'description': f"Columns to exclude from the result set before aggregation is applied."
                        },
                        # TODO target_criteria_predicate, target_criteria_value
                    },
                    "required": ["aggregate_function", "target_column"]
                },
                "endpoint": "/aggregate"
            },
            {
                "name": "get_columns",
                "description": "Returns an array of [column_names, dtypes] from a tablular data file.",
                "parameters": {
                    'type': "object",
                    'properties': {
                        'json_data': {
                            'type': 'array',
                            'items': { 'type': 'object', 'minItems': 1},
                            'description': 'A list of objects with the structure { column_name: value, ... }. If this value is provided and is valid it will be used instead of the file.'
                        },
                    },
                    'required': []
                },
                "endpoint": "/columns"
            },
           ]

@app.get("/health")
async def health():
    return {"status": "ok"}

from file_utils import handle_upload, get_supported_filetypes

@app.get('/upload')
async def supported_filetypes():
    return get_supported_filetypes()

@app.post('/upload/{user_id}/{chat_id}')
async def upload_file(user_id:int, chat_id:int, file: UploadFile = File(...)):
    return await handle_upload(user_id, chat_id, file)