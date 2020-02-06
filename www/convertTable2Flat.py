#!/usr/bin/python3

import sys
import csv
import re

# Check to see if a file name is specified on the command line.
if(len(sys.argv)<2) :
    print("error, need to include one filename on the command line.\n%{0} copus.csv".format(sys.argv[0]))
    exit(1)

# Set up some of the searches that will be used later.
isStudent    = re.compile(r'^stud_')
isInstructor = re.compile(r'^inst_')
isEngagement = re.compile(r'^engage_')

# Routine to determine if a name is located in a list. If not it
# returns -1.  If the string does exist it returns the number in the
# list where it is located.
def determinePosition(header,title) :
    for column in header:
        if(re.search(title,column)):
            return(header.index(column))
    return(None)


# First open the files and get the header
roster = open(sys.argv[1],"r")
reader = csv.reader(roster)
header = next(reader)

periodPos = determinePosition(header,"period")

# Now go through each row.
print("period,group,activity,engagement")
for row in reader:
    for lupe in range(len(row)):
        if(row[lupe]=='1'):
            type = ""
            engagement = ""
            code = ""
            if(isInstructor.search(header[lupe])):
                type = "lecturer"
                code = isInstructor.sub('',header[lupe])
            elif(isStudent.search(header[lupe])):
                type = "student"
                code = isStudent.sub('',header[lupe])
                for engageLupe in range(len(header)):
                    if (re.search(header[lupe],header[engageLupe]) and isEngagement.search(header[engageLupe])):
                        engagement = row[engageLupe]
                        break
            print("{0},{1},{2},{3}".format(row[periodPos],type,code,engagement))
roster.close()
