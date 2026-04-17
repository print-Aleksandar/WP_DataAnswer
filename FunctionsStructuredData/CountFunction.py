import pandas as pd

def FunctionCount(FileJson, ColumnsFiltersOperators):

    df = pd.DataFrame(FileJson)
    count = 0

    for index, row in df.iterrows(): # iterrows = row by row
        flag = True
        for column, filter_value, operator in ColumnsFiltersOperators:
            if operator == "==":
                if row[column] != filter_value:
                    flag = False
                    break
            elif operator == ">":
                if row[column] <= filter_value:
                    flag = False
                    break
            elif operator == ">=":
                if row[column] < filter_value:
                    flag = False
                    break
            elif operator == "<":
                if row[column] >= filter_value:
                    flag = False
                    break
            elif operator == "<=":
                if row[column] > filter_value:
                    flag = False
                    break
        if flag:
            count += 1

    return count