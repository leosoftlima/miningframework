# This script receives as input the path to a directory generated by the miningframework, it reads the output files and creates a [output]/data/results-soot.csv with the output in a format suported by a SOOT analysis framework

import sys

CLASS_NAME = "className"
LEFT_MODIFICATION = "leftModification"
RIGHT_MODIFICATION = "rightModfication"
COMMIT_SHA = "commitSha"
PROJECT_NAME = "projectName"

outputPath = sys.argv[1] # get output path passed as cli argument
def exportCsv():
    f = open(outputPath + "/data/results.csv", "r")
    file = f.read()
    f.close()

    bruteLines = file.split("\n")

    parsed = parse_output(bruteLines)
    csv = generate_csv(parsed)


def parse_output(lines):
    result = []
    for line in lines[1:]:
        cells = line.split(";")
        if (len (cells) > 1):
            method = {}
            method[PROJECT_NAME] = cells[0]
            method[COMMIT_SHA] = cells[1]
            method[CLASS_NAME] = cells[2]
            method[LEFT_MODIFICATION] = parse_modification(cells[4])
            method[RIGHT_MODIFICATION] = parse_modification(cells[6])
            result.append(method)
    return result

def parse_modification(modifications):
    trimmedInput = modifications.strip("[]").replace(" ", "")
    if (len (trimmedInput) > 0):
        return trimmedInput.split(",")
    return []

def generate_csv(collection):
    for elem in collection:
        result = []
        resultReverse = []
        className = elem[CLASS_NAME]
        leftMods = elem[LEFT_MODIFICATION]
        rightMods = elem[RIGHT_MODIFICATION]
        for l in leftMods:
            resultReverse.append(className + ",sink," + l)
            result.append(className + ",source," + l)
        for r in rightMods:
            resultReverse.append(className + ",source," + r)
            result.append(className + ",sink," + r)
        try:
            basePath = outputPath + "/files/" + elem[PROJECT_NAME] + "/" + elem[COMMIT_SHA]
            saveFile(basePath + "/soot.csv", result)
            saveFile(basePath + "/soot-reverse.csv", resultReverse)
        except:
            pass

def saveFile(filePath, result):
    csvFile = open(filePath, "w")
    csvFile.write("\n".join(result))
    csvFile.close()

exportCsv()