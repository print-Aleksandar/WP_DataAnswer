import operator
import numpy as np
import pandas as pd

TRANSFORM_MAP = {
    'to_upper': lambda s: s.astype(str).str.upper(),
    'to_lower': lambda s: s.astype(str).str.lower(),
    'round': lambda s: pd.to_numeric(s, errors='coerce').round(),
    'absolute': lambda s: pd.to_numeric(s, errors='coerce').abs(),
    'add': lambda s, v: pd.to_numeric(s, errors='coerce') + float(v),
    'multiply': lambda s, v: pd.to_numeric(s, errors='coerce') * float(v),
    'divide': lambda s, v: pd.to_numeric(s, errors='coerce') / float(v),
    'subtract': lambda s, v: pd.to_numeric(s, errors='coerce') - float(v),
    'encode_boolean': lambda s, v=None: s.astype(bool).map({True: 1, False: 0}),
    'decode_boolean': lambda s, v=None: s.astype(float).map({1.0: True, 0.0: False}),
}
NUMERIC_TRANSFORMS = ['round', 'absolute', 'add', 'multiply', 'divide', 'subtract', 'decode_boolean']
TEXT_TRANSFORMS = ['to_upper', 'to_lower']
BOOLEAN_TRANSFORMS = ['encode_boolean']
VALUE_NEEDED_TRANSFORMS = ['add', 'multiply', 'divide', 'subtract']

PREDICATE_MAP = {
    'eq': operator.eq,
    'ne': operator.ne,
    'gt': operator.gt,
    'lt': operator.lt,
    'gte': operator.ge,
    'lte': operator.le,
    'contains': lambda s, v: s.astype(str).str.contains(v, case=True, na=False),
    'containsIgnoreCase': lambda s, v: s.astype(str).str.contains(v, case=False, na=False),
    'equalsIgnoreCase': lambda s, v: s.astype(str).str.lower() == str(v).lower(),
}
NUMERIC_PREDICATES = ['eq', 'ne', 'gt', 'lt', 'gte', 'lte']
TEXT_PREDICATES = ['eq', 'ne', 'contains', 'containsIgnoreCase', 'equalsIgnoreCase']
BOOLEAN_PREDICATES = ['eq', 'ne']

AGG_MAP = {
    'sum': np.sum,
    'average': np.mean,
    'count': 'count',
    'unique_count': 'nunique',
    'max': np.max,
    'min': np.min,
    'std_dev': np.std,
    'median': np.median
}
AGG_NUMERIC_FUNCS = ['sum', 'average', 'count', 'unique_count', 'max', 'min', 'std_dev', 'median']
AGG_NON_NUMERIC_FUNCS = ['count', 'nunique']