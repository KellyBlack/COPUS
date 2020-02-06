
# Define a function to make a Pareto Chart
pareto = function(d,sortOrder,sortAtAll,theTitle,xlabel,cutoff=-1)
  {
    d <- table(d,exclude=c(""))

    if(sortAtAll)
      {
        freq = sort(d,decreasing=sortOrder)
      }
    else
      {
        freq = d
      }

    if((cutoff>0) && (cutoff<length(freq)))
      {
        freq <- freq[1:cutoff]
      }
    
    b = barplot(freq,xlab=xlabel,ylab='Frequency',
      ylim=c(0,1.1*sum(freq)),names=names(freq),
      main=theTitle,las=2)
    lines(b,cumsum(freq))
    points(b,cumsum(freq))

    return(d)
  }


activities <- read.csv('copus_flatfile.csv',colClasses = "character")
activities$activity <- as.character(activities$activity)

lecturer <- as.factor(activities$activity[activities$group=='lecturer'])
studentActivity <- as.factor(activities$activity[activities$group=='student'])
studentEngagement <- activities$engagement[activities$group=='student']


barplot(table(lecturer),
        main="Observations of the Instructor",
        xlab="Actions",
        ylab="Frequency")

pareto(lecturer,TRUE,TRUE,
       "Observations of the Instructor",
       "Actions",cutoff=-1)
    
spineplot(as.factor(studentEngagement)~as.factor(studentActivity),
          main="Observations of the Students",
          xlab="Student Activities",ylab="Levels of Engagement")

mosaicplot(table(studentActivity,studentEngagement),
           main="Observations of the Students",
           xlab="Student Activities",ylab="Levels of Engagement")
