

activities <- read.csv('copus_flatfile.csv',colClasses = "character")
a$activity <- as.character(a$activity)

lecturer <- as.factor(a$activity[a$group=='lecturer'])
studentActivity <- as.factor(a$activity[a$group=='student'])
studentEngagement <- a$engagement[a$group=='student']


barplot(table(lecturer),main="Observations of the Instructor",xlab="Actions")
mosaicplot(table(studentActivity,studentEngagement),main="Observations of the Students",
           xlab="Student Activities",ylab="Levels of Engagement")
