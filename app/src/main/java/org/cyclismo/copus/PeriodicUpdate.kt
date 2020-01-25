package org.cyclismo.copus

import kotlin.math.abs

/*
    Class to keep track of what is happening in a classroom.
    Maintains a record of what is clicked.
    Categories based on those found at https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3846513/

    Example table from the paper:
    Table 3.
    Average Jaccard similarity scores for COPUS codes across all pairs observing in all courses for both UBC faculty observers and Maine K–12 teacher observers;
    numbers closer to 1 indicate the greatest similarity between two observers

    Student code                                    UBC         UMaine       Instructor  code                   UBC     UMaine
    L: Listening                                   	0.95    	0.96	     Lec: Lecturing                 	0.91	0.92
    Ind: Individual thinking/problem solving       	0.97    	0.91	     RtW: Real-time writing         	0.93	0.93
    CG: Discuss clicker question                   	0.98    	0.97	     FUp: Follow-up on clicker      	0.92	0.85
                                                                                  questions or activity
    WG: Working in groups on worksheet activity   	0.98    	0.99	     PQ: Posing nonclicker questions	0.86	0.80
    OG: Other group activity                       	Not used	0.97	     CQ: Asking a clicker question  	0.93	0.97
    AnQ: Students answer question posed by      	0.91    	0.84	     AnQ: Answering student questions	0.94	0.89
        instructor
    SQ: Student asks question                      	0.96    	0.93	     MG: Moving through the class   	0.96	0.97
    WC: Engaged in whole-class discussion          	0.96    	0.98	     1o1: One-on-one discussions    	0.94	0.96
                                                                                  with students
    Prd: Making a prediction about the outcome     	Not used	1.00	     D/V: Conducting a demo,        	0.97	0.98
         of demo or experiment                                                    experiment, etc.
    SP: Presentation by students^a              	Not used	Not used	 Adm: Administration            	0.94	0.97
    TQ: Test or quiz^a                          	Not used	Not used	 W: Waiting                     	0.95	0.98
    W: Waiting                                  	0.99    	0.98	     O: Other                       	0.97	1.00
    O: Other                                    	0.94    	0.99

    a“SP: Presentation by students” and “TQ: Test/quiz” were not selected in any of the observations at UBC or UMaine.
    This result likely occurred because when we asked UBC and UMaine faculty members if we could observe their classes,
    we also asked them if there was anything unusual going on in their classes that day. We avoided classes with student
    presentations and tests/quizzes, because these situations would limit the diversity of codes that could be selected
    by the observers.
 */

class PeriodicUpdate
{

    private var running : Boolean = false;
    public var startTime : Long = 0

    private val lecturerCode : MutableMap<String,Boolean> =
        mutableMapOf<String,Boolean>(
            "Lec" to false,
            "RtW" to false,
            "FUp" to false,
            "PQ" to false,
            "CQ" to false,
            "AnQ" to false,
            "MG" to false,
            "1o1" to false,
            "DV" to false,
            "ADM" to false,
            "W" to false,
            "O" to false
        )

    private val studentCode : MutableMap<String,Boolean> =
        mutableMapOf<String,Boolean>(
            "L" to false,
            "Ind" to false,
            "CG" to false,
            "WG" to false,
            "OG" to false,
            "AnQ" to false,
            "SQ" to false,
            "WC" to false,
            "Prd" to false,
            "SP" to false,
            "TQ" to false,
            "W"  to false,
            "O" to false
        )

    private val studentEngagement : MutableMap<String,String> =
        mutableMapOf<String,String>(
            "L" to "",
            "Ind" to "",
            "CG" to "",
            "WG" to "",
            "OG" to "",
            "AnQ" to "",
            "SQ" to "",
            "WC" to "",
            "Prd" to "",
            "SP" to "",
            "TQ" to "",
            "W"  to "",
            "O" to ""
        )


    constructor()
    {
        //println("Starting up PeriodicUpdate")
        this.running = false
        clearAllValues()
    }

    fun clearAllValues()
    {
        for((keyValue,value) in lecturerCode)
        {
            lecturerCode.set(keyValue,false)
        }
        for((keyValue,value) in studentCode)
        {
            studentCode.set(keyValue,false)
        }

    }

    fun turnOver(currentTime : Long) : Boolean
    {
        if(abs(currentTime-this.startTime)>120000)
        {
            return(true)
        }
        return(false)
    }
    fun runTimer(period:Int=100)
    {
        this.running = true;
        println("run timer ${this.running}")
    }

    fun stopTimer(period:Int=100)
    {
        this.running = false;
        println("stop timer ${this.running}")
    }

    fun getLecturerValue(keyValue : String) : Boolean
    {
        if(lecturerCode.containsKey(keyValue))
        {
            return(lecturerCode.get(keyValue) ?: false)
        }
        return(false)
    }

    fun setLecturerValue(keyValue : String,newValue : Boolean) : Boolean
    {
        if(lecturerCode.containsKey(keyValue))
        {
            lecturerCode.set(keyValue,newValue)
            println("Set lecture code ${keyValue} to ${newValue}")
            return(true)
        }
        return(false)
    }

    fun getStudentValue(keyValue : String) : Boolean
    {
        if(studentCode.containsKey(keyValue))
        {
            return(studentCode.get(keyValue) ?: false)
        }
        return(false)
    }

    fun setStudentValue(keyValue : String,newValue : Boolean) : Boolean
    {
        if(studentCode.containsKey(keyValue))
        {
            println("Set student code ${keyValue} to ${newValue}")
            studentCode.set(keyValue,newValue)
            return(true)
        }
        return(false)
    }

    fun getEngagementValue(keyValue : String) : String
    {
        if(studentEngagement.containsKey(keyValue))
        {
            return(studentEngagement.get(keyValue) ?: "")
        }
        return("")
    }

    fun setEngagementValue(keyValue : String,newValue : String) : Boolean
    {
        if(studentEngagement.containsKey(keyValue))
        {
            studentEngagement.set(keyValue,newValue)
            println("Set student engagement code ${keyValue} to ${newValue}")
            return(true)
        }
        return(false)
    }

    public fun convertToString() : String
    {
        var allValues : String = ""
        for((key,value) in lecturerCode)
        {
            allValues += "$value,"
        }
        for((key,value) in studentCode)
        {
            allValues += "$value,"
        }
        for((key,value) in studentEngagement)
        {
            allValues += "$value,"
        }
        return(allValues)

    }

    public fun headerToString() : String
    {
        var allValues : String = ""
        for((key,value) in lecturerCode)
        {
            allValues += "$key,"
        }
        for((key,value) in studentCode)
        {
            allValues += "$key,"
        }
        for((key,value) in studentEngagement)
        {
            allValues += "engagement_$key,"
        }
        return(allValues)

    }


}