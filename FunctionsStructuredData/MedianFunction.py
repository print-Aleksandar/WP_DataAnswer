import pandas as pd

def FunctionMedian(FileJson, SelectFilter, ValueColumn):

    #FileJson is a JSON format of a table
    #SelectFilter is Filter/s for which column/s to perform
    #ValueColumn - in which column using filters to take the values

    df = pd.DataFrame(FileJson)
    listValues = []

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
            listValues.append(row[ValueColumn])

    if len(listValues) == 0:
        return 0

    listValues.sort()

    if len(listValues)%2==0:
        element1 = listValues[(len(listValues)//2)-1]
        element2 = listValues[len(listValues)//2]
        return (element1+element2)/2
    else:
        return listValues[len(listValues)//2]