import pandas as pd

def FunctionSum(FileJson, SelectFilter, SumColumn):


    # FileJson is a JSON format of a table
    # SelectFilter is Filter/s for which column/s to perform
    # SumColumn - in which column using filters to find sum

    df = pd.DataFrame(FileJson)
    sum = 0

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
            sum += row[SumColumn]

    return sum