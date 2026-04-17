import pandas as pd

def FunctionMax(FileJson, SelectFilter, MaxColumn):

    #FileJson is a Json format of a table
    #SelectFilter is Filter/s for which column/s to perform
    #MaxColumn - in which column using filters to find max value

    df = pd.DataFrame(FileJson)
    maxValue = float("-inf")

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
            maxValue = max(maxValue, row[MaxColumn])

    return maxValue