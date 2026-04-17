import pandas as pd

def FunctionBool(FileJson, SelectFilter):

    #FileJson is a Json format of a table
    #SelectFilter is Filter/s for which column/s to perform

    df = pd.DataFrame(FileJson)

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
            return True

    return False