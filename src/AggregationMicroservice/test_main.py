import pytest
from fastapi.testclient import TestClient
import json
from main import app

client = TestClient(app)

def test_transform_lowercase_and_multiply_then_sum():
    """
    Transforms: dept to_lower, rev multiply x1000
    Filter: dept == 'sales'
    Expected: (5*1000) + (2*1000) = 7000.0  (hr row dropped by filter)
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "transforms": [
            ["dept", "to_lower", ""],
            ["rev",  "multiply", 1000]
        ],
        "filters": [["dept", "eq", "sales"]],
        "aggregate_function": "sum",
        "target_column": "rev"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    assert r.json()["result"]["result"] == 7000.0

def test_transform_multiply_then_group_and_criteria():
    """
    Transforms: rev multiply x1000
    No pre-filter (all rows pass)
    Group by dept, sum rev
    Target criteria: only groups where sum > 4000
    Expected groups after multiply:
        SALES -> 5000 + 2000 = 7000  (passes > 4000)
        hr    -> 3000               (fails > 4000... wait 3000 < 4000, dropped)
    Result: 1 record, SALES=7000
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "transforms": [["rev", "multiply", 1000]],
        "aggregate_function": "sum",
        "target_column": "rev",
        "grouping_columns": ["dept"],
        "target_criteria_predicate": "gt",
        "target_criteria_value": 4000
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    data = json.loads(r.json()["result"])
    assert len(data) == 1
    assert data[0]["dept"] == "SALES"
    assert data[0]["rev"] == 7000.0

def test_no_transforms_plain_sum():
    """
    No transforms, no filters.
    Sum all rev: 5 + 3 + 2 = 10
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "aggregate_function": "sum",
        "target_column": "rev"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    assert r.json()["result"]["result"] == 10.0

def test_filter_before_aggregate_no_transform():
    """
    Filter region == 'North' first, then sum rev.
    Rows kept: SALES(5) + hr(3) = 8
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "filters": [["region", "eq", "North"]],
        "aggregate_function": "sum",
        "target_column": "rev"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    assert r.json()["result"]["result"] == 8.0

def test_target_criteria_meets():
    """
    Sum all rev = 10. Check if > 5 -> meets_criteria = True
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "aggregate_function": "sum",
        "target_column": "rev",
        "target_criteria_predicate": "gt",
        "target_criteria_value": 5
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    result = r.json()["result"]
    assert result["result"] == 10.0
    assert result["meets_criteria"] is True

def test_target_criteria_fails():
    """
    Sum all rev = 10. Check if > 50 -> meets_criteria = False
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "aggregate_function": "sum",
        "target_column": "rev",
        "target_criteria_predicate": "gt",
        "target_criteria_value": 50
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    result = r.json()["result"]
    assert result["result"] == 10.0
    assert result["meets_criteria"] is False

def test_invalid_aggregate_function_for_numeric():
    """
    'nunique' is not a valid key in AGG_MAP — should return 400.
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"}
        ],
        "aggregate_function": "nunique",
        "target_column": "rev"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 400

def test_divide_by_zero_rejected():
    """
    divide by 0 should be caught in is_transform_valid -> 400
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"}
        ],
        "transforms": [["rev", "divide", 0]],
        "aggregate_function": "sum",
        "target_column": "rev"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 400

def test_exclude_target_column_rejected():
    """
    Excluding the target column should raise -> 400
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"}
        ],
        "aggregate_function": "sum",
        "target_column": "rev",
        "exclude_cols": ["rev"]
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 400

def test_nonexistent_target_column_rejected():
    """
    Asking to aggregate a column that doesn't exist -> 400
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"}
        ],
        "aggregate_function": "sum",
        "target_column": "profit"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 400

def test_transform_order_matters():
    """
    multiply rev x1000 THEN filter rev > 4000.
    Without transform, 5 > 4000 is False. With it, 5000 > 4000 is True.
    Verifies transforms run before filters.
    Rows passing: SALES-North(5000), SALES-South(2000 - fails), hr(3000 - fails)
    Sum = 5000
    """
    payload = {
        "json_data": [
            {"dept": "SALES", "rev": 5, "region": "North"},
            {"dept": "hr",    "rev": 3, "region": "North"},
            {"dept": "SALES", "rev": 2, "region": "South"}
        ],
        "transforms": [["rev", "multiply", 1000]],
        "filters": [["rev", "gt", 4000]],
        "aggregate_function": "sum",
        "target_column": "rev"
    }
    r = client.post("/aggregate/user456", json=payload)
    assert r.status_code == 200
    assert r.json()["result"]["result"] == 5000.0