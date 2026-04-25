from static import *
import pandas as pd
from pandas.core.dtypes.common import is_numeric_dtype, is_string_dtype, is_object_dtype, is_bool_dtype
import numbers

class Aggregator:
    def __init__(self,
                 json_data,
                 aggregate_function,
                 target_column,
                 filters=None,
                 target_criteria_predicate=None,
                 target_criteria_value=None,
                 grouping_columns=None,
                 transforms=None,
                 exclude_cols=None):
        if isinstance(json_data, list):
            self.df = pd.DataFrame(json_data)
        else:
            self.df = pd.read_json(json_data)
        self.filters = filters
        self.aggregate_function = aggregate_function
        self.target_column = target_column
        self.target_criteria_predicate = target_criteria_predicate
        self.target_criteria_value = target_criteria_value
        self.grouping_columns = grouping_columns
        self.transforms = transforms
        self.exclude_cols = exclude_cols

    def drop_cols(self):
        if self.exclude_cols is None:
            return

        if self.target_column in self.exclude_cols:
            raise Exception('Target column cannot be excluded')

        cols_to_keep = [col for col in self.df.columns if col not in self.exclude_cols]
        self.df.drop(columns=cols_to_keep, inplace=True)
        self.df.reset_index(drop=True, inplace=True)

    def transform_columns(self):
        bool_cols = [col for col in self.df.columns if is_bool_dtype(self.df[col])]
        self.df[bool_cols] = self.df[bool_cols].fillna(False)

        str_cols = [col for col in self.df.columns if is_object_dtype(self.df[col])
                     or is_string_dtype(self.df[col])]
        self.df[str_cols] = self.df[str_cols].fillna('Undefined')

        numeric_cols = [col for col in self.df.columns if is_numeric_dtype(self.df[col])]
        self.df[numeric_cols] = self.df[numeric_cols].fillna(0)

        if self.transforms is None:
            return

        for column_name, transform, value in self.transforms:
            if not self.is_transform_valid(column_name, transform, value):
                raise Exception(f'Invalid transform: {column_name, transform, value}')

            func = TRANSFORM_MAP[transform]
            if transform in VALUE_NEEDED_TRANSFORMS:
                self.df[column_name] = func(self.df[column_name], value)
            else:
                self.df[column_name] = func(self.df[column_name])

    def apply_filters(self):
        if self.filters is None:
            return

        for column_name, predicate, value in self.filters:
            if not self.is_filter_valid(column_name, predicate, value):
                raise Exception(f'Invalid filter: {column_name, predicate, value}')

            func = PREDICATE_MAP[predicate]
            mask = func(self.df[column_name], value)
            self.df = self.df[mask]

    def get_result(self):
        self.drop_cols()
        self.transform_columns()
        self.apply_filters()

        if self.target_column not in self.df.columns:
            raise Exception('Target column does not exist')

        if is_numeric_dtype(self.df[self.target_column]):
            if self.aggregate_function not in AGG_NUMERIC_FUNCS:
                raise Exception(f'Invalid aggregate function: {self.aggregate_function}'
                                f' for non-numeric column: {self.target_column}')

        else:
            if self.aggregate_function not in AGG_NON_NUMERIC_FUNCS:
                raise Exception(f'Invalid aggregate function: {self.aggregate_function}'
                                f' for numeric column: {self.target_column}')

        func = AGG_MAP[self.aggregate_function]

        if self.grouping_columns is not None:
            result = self.df.groupby(self.grouping_columns)[self.target_column].agg(func).reset_index()
            if (self.target_criteria_predicate is not None
                    and self.target_criteria_value is not None):
                criteria_func = PREDICATE_MAP[self.target_criteria_predicate]
                mask = criteria_func(result[self.target_column], self.target_criteria_value)
                result = result[mask]

            return result.to_json(orient='records')

        else:
            result = self.df[self.target_column].agg(func)
            try:
                result = result.item()
            except TypeError:
                pass
            if (self.target_criteria_predicate is None
                    or self.target_criteria_value is None):
                return {"result": result}
            else:
                target_criteria_func = PREDICATE_MAP[self.target_criteria_predicate]
                dummy_series = pd.Series([result])
                meets_criteria = bool(target_criteria_func(dummy_series, self.target_criteria_value).iloc[0])
                return {"result": result, "meets_criteria": meets_criteria}

    def is_transform_valid(self, column_name, transform, value):
        if column_name is None or transform is None:
            return False

        if transform not in TRANSFORM_MAP:
            return False

        needs_value = transform in VALUE_NEEDED_TRANSFORMS

        if needs_value and value is None:
            return False

        if transform == 'divide':
            try:
                if float(value) == 0:
                    return False
            except (TypeError, ValueError):
                return False

        if is_numeric_dtype(self.df[column_name]):
            return transform in NUMERIC_TRANSFORMS

        if is_string_dtype(self.df[column_name]) or is_object_dtype(self.df[column_name]):
            return transform in TEXT_TRANSFORMS

        if is_bool_dtype(self.df[column_name]):
            return transform in BOOLEAN_TRANSFORMS

        return False

    def is_filter_valid(self, column_name, predicate, value):
        if column_name is None or predicate is None or value is None:
            return False

        if predicate not in PREDICATE_MAP.keys():
            return False

        if is_numeric_dtype(self.df[column_name]):
            if predicate in NUMERIC_PREDICATES:
                if isinstance(value, numbers.Number):
                    return True

        if is_string_dtype(self.df[column_name]) \
                or is_object_dtype(self.df[column_name]):
            if predicate in TEXT_PREDICATES:
                if isinstance(value, str):
                    return True

        if is_bool_dtype(self.df[column_name]):
            if predicate in BOOLEAN_PREDICATES:
                if isinstance(value, bool):
                    return True

        return False