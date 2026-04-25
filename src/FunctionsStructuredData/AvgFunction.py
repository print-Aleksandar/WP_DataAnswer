import SumFunction
from FunctionsStructuredData import CountFunction


def FunctionAvg(FileJson, SelectFilter, AvgColumn):

    #FileJson is a JSON format of a table
    #SelectFilter is Filter/s for which column/s to perform
    #AvgColumn - in which column using filters to find sum

    sum = SumFunction.FunctionSum(FileJson, SelectFilter, AvgColumn)
    count = CountFunction.FunctionCount(FileJson, SelectFilter)

    if count == 0:
        return 0

    return round(sum/count, 3)