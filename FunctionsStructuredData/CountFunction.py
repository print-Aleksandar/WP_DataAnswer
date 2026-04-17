import pandas as pd

def FunctionCount(FileJson, SelectFilter):

    # FileJson is a JSON format of a table
    # SelectFilter is Filter/s for which column/s to perform

    df = pd.DataFrame(FileJson)
    count = 0

    for index, row in df.iterrows(): # iterrows = row by row
        flag = True
        for column, filter_value, operator in SelectFilter:
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