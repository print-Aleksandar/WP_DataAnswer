import pandas as pd

def FunctionMin(FileJson, SelectFilter, MinColumn):

    #FileJson is a JSON format of a table
    #SelectFilter is Filter/s for which column/s to perform
    #MaxColumn - in which column using filters to find min value

    df = pd.DataFrame(FileJson)
    minValue = float("inf")

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
            minValue = min(minValue, row[MinColumn])

    return minValue