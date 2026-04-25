from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Any, Optional, Tuple
from agg_cl import Aggregator

app = FastAPI()

class AggregationRequest(BaseModel):
    json_data: List[dict]
    aggregate_function: str
    target_column: str
    filters: Optional[List[Tuple[str, str, Any]]] = None
    grouping_columns: Optional[List[str]] = None
    target_criteria_predicate: Optional[str] = None
    target_criteria_value: Optional[Any] = None
    transforms: Optional[List[Tuple[str, str, Any]]] = None
    exclude_cols: Optional[List[str]] = None

@app.post("/aggregate/{id_user}")
async def run_aggregator(id_user: str, request: AggregationRequest):
    try:
        agg = Aggregator(
            json_data=request.json_data,
            aggregate_function=request.aggregate_function,
            target_column=request.target_column,
            filters=request.filters,
            target_criteria_predicate=request.target_criteria_predicate,
            target_criteria_value=request.target_criteria_value,
            grouping_columns=request.grouping_columns,
            transforms=request.transforms,
            exclude_cols=request.exclude_cols
        )

        result = agg.get_result()
        return {"user_id": id_user, "result": result}

    except Exception as e:
        print(e)
        raise HTTPException(status_code=400, detail=str(e))