package org.cyclismo.copus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.DocumentsContract
//import android.util.Log
//import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*

import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.nio.charset.Charset


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener
{
    val TESTING: Boolean = true

    var currentObservation : PeriodicUpdate = PeriodicUpdate()
    var pastObservations : MutableList<PeriodicUpdate> = mutableListOf<PeriodicUpdate>()
    var startButton : Button? = null

    var checkBoxIDs = arrayOf<Int>()
    var spinnerBoxIds = arrayOf<Int>()

    var checkBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()
    var spinnerBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()

    var checkBoxLecturerIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    var checkBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    var spinnerBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //    .setAction("Action", null).show()
            sendResults(view)
        }

        this.startButton = findViewById<Button>(R.id.button)
        currentObservation.clearAllValues()
        clearAllCheckboxes()

        this.checkBoxIDs = arrayOf(
            R.id.lecturingCheckbox,
            R.id.rtwCheckbox,
            R.id.fupCheckbox,
            R.id.pqCheckbox,
            R.id.cqCheckbox,
            R.id.anqCheckbox,
            R.id.mgCheckbox,
            R.id.oneOnOneCheckbox,
            R.id.dvCheckbox,
            R.id.adminCheckbox,
            R.id.waitingCheckbox,
            R.id.otherCheckbox,
            R.id.studentListeningCheckbox,
            R.id.individualCheckbox,
            R.id.cgCheckbox,
            R.id.wgCheckbox,
            R.id.ogCheckbox,
            R.id.studentAnswerCheckbox,
            R.id.sqCheckbox,
            R.id.wcCheckbox,
            R.id.prdCheckbox,
            R.id.spCheckbox,
            R.id.tqCheckbox,
            R.id.studentwaitingCheckbox,
            R.id.studentotherCheckbox
        )

        this.spinnerBoxIds = arrayOf(
            R.id.ListeningSpinner,
            R.id.ThinkingSpinnerind,
            R.id.CGSpinner,
            R.id.groupWorkSpinner,
            R.id.ogSpinner,
            R.id.ANQSpinner,
            R.id.SQSpinner,
            R.id.WCspinner,
            R.id.PRDspinner,
            R.id.SPSpinner,
            R.id.TQSpinner,
            R.id.waitingSpinner,
            R.id.otherSpinner
        )

        this.checkBoxLecturerIdentifiers = mutableMapOf<Int,String>(
            R.id.lecturingCheckbox to "Lec",
            R.id.rtwCheckbox  to "RtW",
            R.id.fupCheckbox to "FUp",
            R.id.pqCheckbox to "PQ",
            R.id.cqCheckbox to "CQ",
            R.id.anqCheckbox to "AnQ",
            R.id.mgCheckbox to "MG",
            R.id.oneOnOneCheckbox to "1o1",
            R.id.dvCheckbox to "DV",
            R.id.adminCheckbox to "ADM",
            R.id.waitingCheckbox to "W",
            R.id.otherCheckbox to "O"
        )

        this.checkBoxStudentIdentifiers = mutableMapOf<Int,String>(
            R.id.studentListeningCheckbox to "L",
            R.id.individualCheckbox to "Ind",
            R.id.cgCheckbox to "CG",
            R.id.wgCheckbox to "WG",
            R.id.ogCheckbox to "OG",
            R.id.studentAnswerCheckbox to "AnQ",
            R.id.sqCheckbox to "SQ",
            R.id.wcCheckbox to "WC",
            R.id.prdCheckbox to "Prd",
            R.id.spCheckbox to "SP",
            R.id.tqCheckbox to "TQ",
            R.id.studentwaitingCheckbox to "W",
            R.id.studentotherCheckbox to "O"
        )

        this.spinnerBoxStudentIdentifiers = mutableMapOf<Int,String>(
            R.id.ListeningSpinner to "L",
            R.id.ThinkingSpinnerind to "Ind",
            R.id.CGSpinner to "CG",
            R.id.groupWorkSpinner to "WG",
            R.id.ogSpinner to "OG",
            R.id.ANQSpinner to "AnQ",
            R.id.SQSpinner to "SQ",
            R.id.WCspinner to "WC",
            R.id.PRDspinner to "Prd",
            R.id.SPSpinner to "SP",
            R.id.TQSpinner to "TQ",
            R.id.waitingSpinner to "W",
            R.id.otherSpinner to "O"
        )

        for(checkBoxID in checkBoxIDs)
        {
            this.checkBoxViews.put(findViewById(checkBoxID),checkBoxID)
        }

        for(spinnerBoxID in spinnerBoxIds)
        {
            val view : Spinner = findViewById<Spinner>(spinnerBoxID)
            this.spinnerBoxViews.put(view,spinnerBoxID)

            view.onItemSelectedListener = this@MainActivity
            /*
            view.onItemSelectedListener = object:
                AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    println("Changed ${spinnerBoxStudentIdentifiers[view.id]}")
                }

                override fun onNothingSelected(parent: AdapterView<out Adapter>?) {

                }
            }

             */
        }

    }

    override fun onNothingSelected(parent: AdapterView<out Adapter>?)
    {
        //val theSpinner = parent as Spinner
        this.currentObservation.setEngagementValue(this.spinnerBoxStudentIdentifiers[parent?.id] ?: "","")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long)
    {
        val theSpinner = parent as Spinner
        this.currentObservation.setEngagementValue(this.spinnerBoxStudentIdentifiers[parent.id] ?: "",theSpinner.getSelectedItem().toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun clearAllCheckboxes()
    {

        for(checkboxID in checkBoxIDs)
        {
            val checkbox : CheckBox = findViewById<CheckBox>(checkboxID)
            if(checkbox != null)
                checkbox.isChecked = false
        }

        for(spinnerBoxID in spinnerBoxIds)
        {
            val spinnerBox : Spinner = findViewById<Spinner>(spinnerBoxID)
            if(spinnerBox != null)
                spinnerBox.setSelection(0)
        }

    }

    fun lecturingClick(view : View)
    {
        if(view is CheckBox) {
            println("View: ${view.id} ${view.isChecked}")
            val checkValue : Boolean = view.isChecked
            if(checkBoxLecturerIdentifiers.containsKey(view.id))
            {
                println("This is a lecturer check box")
                currentObservation.setLecturerValue(checkBoxLecturerIdentifiers[view.id] ?: "",checkValue)
            }
            else if (checkBoxStudentIdentifiers.containsKey(view.id))
            {
                println("This is a student check box")
                currentObservation.setStudentValue(checkBoxStudentIdentifiers[view.id] ?: "",checkValue)
            }
            /*
            when (view.id) {
                R.id.lecturingCheckbox -> println("Lecture checkbox")
                R.id.rtwCheckbox       -> println("Writing on board")
                R.id.fupCheckbox       -> println("Asking follow up question")
                R.id.pqCheckbox        -> println("Post question")
                R.id.cqCheckbox        -> println("Check question")
                R.id.anqCheckbox       -> println("Answer question")
                R.id.mgCheckbox        -> println("Moving around")
                R.id.oneOnOneCheckbox  -> println("One on one questions")
                R.id.dvCheckbox        -> println("Demo/Video")
                R.id.adminCheckbox     -> println("administrative activities")
                R.id.waitingCheckbox   -> println("Waiting for response")
                R.id.otherCheckbox     -> println("Other")
                R.id.studentListeningCheckbox -> println("student listening")
                R.id.individualCheckbox       -> println("individual")
                R.id.cgCheckbox        -> println("cg")
                R.id.wgCheckbox        -> println("wg")
                R.id.ogCheckbox        -> println("og")
                R.id.studentAnswerCheckbox    -> println("student answer")
                R.id.sqCheckbox        -> println("sq")
                R.id.wcCheckbox        -> println("wc")
                R.id.prdCheckbox       -> println("prd")
                R.id.spCheckbox        -> println("sp")
                R.id.tqCheckbox        -> println("test quiz")
                R.id.studentwaitingCheckbox -> println("student waiting")
                R.id.studentotherCheckbox   -> println("student Other")
            }
            */
        }
    }

    fun engagementClick(view:View)
    {
        println("Engagement : ")
    }

    fun clickCOPUSurl(view: View)
    {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.COPUS_URL)))
        startActivity(browserIntent)
    }

    fun startButton(view: View)
    {
        if(view is Button) {
            println("This is the start button ${view.text}")
            val counter: Chronometer = findViewById(R.id.TimeView)
            if (view.text == "Start") {
                clearAllCheckboxes()
                view.text = getString(R.string.Timer_End)
                this.currentObservation.runTimer(10)
                counter.format = getString(R.string.Time_Stamp_Label)
                counter.base = SystemClock.elapsedRealtime()
                this.currentObservation.startTime = counter.base
                counter.start()
                //counter.onChronometerTickListener = object: TimerReact
                counter.setOnChronometerTickListener(TimerReact(this))
            } else {
                clearAllCheckboxes()
                pushCurrentState()
                view.text = getString(R.string.Timer_Start)
                this.currentObservation.stopTimer(10)
                counter.stop()
            }
        }
    }

    fun sendResults(view: View)
    {

        var allObservations : String = ""
        if(this.pastObservations.size>0)
            allObservations = pastObservations.first().headerToString() + "\n"

        for(pastObs in this.pastObservations)
        {
            allObservations += pastObs.convertToString() + "\n"
        }
        println(allObservations)

        saveStringAsFile(allObservations)


        val fileToWrite : File = saveStringAsFile(allObservations)
        println("File to attach: ${fileToWrite.name}")

        if(fileToWrite!=null)
        {
            var email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_SUBJECT, "COPUS Observation")
            email.putExtra(
                Intent.EXTRA_TEXT,
                "The attachement includes the results from the observation."
            )
            email.setType("message/rfc822");
            email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToWrite));
            //email.putExtra(Intent.EXTRA_STREAM, FileInputStream(fileToWrite));
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }

    }

    fun saveStringAsFile(contents:String) : File
    {
        //openFileOutput
        var internalPath : File = filesDir
        /*
        if(TESTING)
        {
            internalPath = File("/tmp/copus/files")
        }
        else {
            internalPath = filesDir
        }
         */
        println("Save string\n ${internalPath.name}")

        val directoryFile: File = File(filesDir,"copus")
        if(directoryFile.mkdirs())
        {
            println("Created directories")
        }
        else
        {
            println("Did not create directores")
        }

        val fileToWrite : File = File(directoryFile,"copus.csv")
        var outputStream : FileOutputStream = FileOutputStream(fileToWrite)
        //outputStream = openFileOutput("copusTempFile", Context.MODE_PRIVATE)
        outputStream.write(contents.toByteArray(Charset.defaultCharset()),0,contents.length)
        outputStream.flush()
        outputStream.close()


        return(File(internalPath,"copus.csv"))
    }

    public fun pushCurrentState()
    {
        this.pastObservations.add(this.currentObservation)
        this.currentObservation = PeriodicUpdate()
        clearAllCheckboxes()
    }


    // from https://developer.android.com/training/data-storage/shared/documents-files
    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "copus.csv")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 1)
    }

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, 2)
    }



    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val contentResolver = applicationContext.contentResolver
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

}

