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

class ClassroomActivity(
    var initials: String = "",
    var header: String = "",
    var present: Boolean = false,
    var engagement: String = "",
    var id: Long = -1
)
{
    fun matchInitials(check:String) : Boolean
    {
        return(this.initials == check)
    }

    fun headerVal() : String
    {
        return(this.header)
    }

    fun recording() : String
    {
        if(this.present)
            return("1")
        return("0")
    }

    fun engagementValue() : String
    {
        return(this.engagement)
    }

    fun matchID(value: Long) : Boolean
    {
        return(this.id == value)
    }

}

class PeriodicUpdate
{

    private var running : Boolean = false
    public var startTime : Long = 0

    private val lecturerOptions = mutableListOf<ClassroomActivity>(
        ClassroomActivity(initials="Lec",header="inst_lec"),
        ClassroomActivity(initials="RtW",header="inst_rtw"),
        ClassroomActivity(initials="FUp",header="inst_fup"),
        ClassroomActivity(initials="PQ",header="inst_pq"),
        ClassroomActivity(initials="CQ",header="inst_cq"),
        ClassroomActivity(initials="AnQ",header="inst_anq"),
        ClassroomActivity(initials="MG",header="inst_mg"),
        ClassroomActivity(initials="1o1",header="inst_1o1"),
        ClassroomActivity(initials="DV",header="inst_dv"),
        ClassroomActivity(initials="ADM",header="inst_adm"),
        ClassroomActivity(initials="W",header="inst_w"),
        ClassroomActivity(initials="O",header="inst_o")
    )

    private val studentOptions = mutableListOf<ClassroomActivity>(
        ClassroomActivity(initials="L",header="stud_L"),
        ClassroomActivity(initials="Ind",header="stud_ind"),
        ClassroomActivity(initials="CG",header="stud_cg"),
        ClassroomActivity(initials="WG",header="stud_wg"),
        ClassroomActivity(initials="OG",header="stud_og"),
        ClassroomActivity(initials="AnQ",header="stud_anq"),
        ClassroomActivity(initials="SQ",header="stud_sq"),
        ClassroomActivity(initials="WC",header="stud_wc"),
        ClassroomActivity(initials="Prd",header="stud_prd"),
        ClassroomActivity(initials="SP",header="stud_sp"),
        ClassroomActivity(initials="TQ",header="stud_tq"),
        ClassroomActivity(initials="W",header="stud_w"),
        ClassroomActivity(initials="O",header="stud_o")
    )


    constructor()
    {
        this.running = false
        clearAllValues()
    }

    fun clearAllValues()
    {
        for (entry in this.lecturerOptions)
            entry.present = false

        for (entry in this.studentOptions)
        {
            entry.present = false
            entry.engagement = ""
        }

    }

    fun isClear() : Boolean
    {
        for (entry in this.lecturerOptions)
            if(entry.present) return(false)

        for (entry in this.studentOptions)
            if((entry.present) || (entry.engagement!="")) return(false)

        return(true)
    }

    fun turnOver(currentTime : Long) : Boolean
    {
        if(abs(currentTime-this.startTime)>120000)
        {
            return(true)
        }
        return(false)
    }

    fun runTimer(@Suppress("UNUSED_PARAMETER") period:Int=100)
    {
        this.running = true;
    }

    fun stopTimer(@Suppress("UNUSED_PARAMETER") period:Int=100)
    {
        this.running = false
    }

    fun getLecturerValue(keyValue : String) : Boolean
    {
        for (entry in this.lecturerOptions)
        {
            if(entry.initials == keyValue)
                return(entry.present)
        }
        return(false)
    }

    fun setLecturerValue(keyValue : String,newValue : Boolean) : Boolean
    {
        for (entry in this.lecturerOptions)
        {
            if(entry.initials == keyValue)
            {
                entry.present = newValue
                return (true)
            }
        }
        return(false)

    }

    fun getStudentValue(keyValue : String) : Boolean
    {
        for(entry in this.studentOptions)
        {
            if(entry.initials==keyValue)
            {
                return(entry.present)
            }
        }
        return(false)
    }

    fun setStudentValue(keyValue : String,newValue : Boolean) : Boolean
    {
        for(entry in this.studentOptions)
        {
            if(entry.initials==keyValue)
            {
                entry.present = newValue
                return(true)
            }
        }
        return(false)
    }

    fun getEngagementValue(keyValue : String) : String
    {
        for(entry in this.studentOptions)
        {
            if(entry.initials==keyValue)
            {
                return(entry.engagement)
            }
        }
        return("")
    }

    fun setEngagementValue(keyValue : String,newValue : String) : Boolean
    {
        for(entry in this.studentOptions)
        {
            if(entry.initials==keyValue)
            {
                entry.engagement = newValue
                return(true)
            }
        }
        return(false)
    }

    public fun convertToString(period:Int) : String
    {
        var allValues : String = "$period"

        for (entry in this.lecturerOptions)
        {
            allValues += ",${entry.recording()}"
        }

        for (entry in this.studentOptions)
        {
            allValues += ",${entry.recording()},${entry.engagementValue()}"
        }

        return(allValues)

    }

    public fun headerToString() : String
    {
        var allValues : String = "period"
        for (entry in this.lecturerOptions)
        {
            allValues += ",${entry.headerVal()}"
        }
        for (entry in this.studentOptions)
        {
            allValues += ",${entry.headerVal()},engage_${entry.headerVal()}"
        }

        return(allValues)

    }


}