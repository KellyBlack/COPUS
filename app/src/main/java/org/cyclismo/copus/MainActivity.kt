package org.cyclismo.copus

//import android.util.Log
//import com.google.android.material.snackbar.Snackbar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.charset.Charset


class MainActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener,
    ConfirmDeleteCurrentObservation.DeleteNoticeDialogListener,
    StopTimerDialog.StopNoticeDialogListener
{

    private var currentObservation : PeriodicUpdate = PeriodicUpdate()
    private var pastObservations : MutableList<PeriodicUpdate> = mutableListOf<PeriodicUpdate>()
    private var startButton : Button? = null

    private var checkBoxIDs = arrayOf<Int>()
    private var spinnerBoxIds = arrayOf<Int>()

    private var checkBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()
    private var spinnerBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()

    private var checkBoxLecturerIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    private var checkBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    private var spinnerBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()

    private lateinit var requestFileIntent: Intent
    private lateinit var inputPFD: ParcelFileDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //    .setAction("Action", null).show()
            sendResults(view)
        }

        this.requestFileIntent = Intent(Intent.ACTION_PICK).apply {
            type = "text/csv"
        }

        this.startButton = findViewById<Button>(R.id.button)
        this.currentObservation.clearAllValues()
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

        for(checkBoxID in this.checkBoxIDs)
        {
            this.checkBoxViews.put(findViewById(checkBoxID),checkBoxID)
        }

        for(spinnerBoxID in this.spinnerBoxIds)
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
            R.id.action_settings ->
            {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_about ->
            {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                true
            }
            R.id.action_help ->
            {
                startActivity(Intent(this@MainActivity,HelpActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    public fun clearAllCheckboxes()
    {

        for(checkboxID in this.checkBoxIDs)
        {
            val checkbox : CheckBox? = findViewById<CheckBox>(checkboxID)
            if(checkbox != null)
                checkbox.isChecked = false
        }

        for(spinnerBoxID in this.spinnerBoxIds)
        {
            val spinnerBox : Spinner? = findViewById<Spinner>(spinnerBoxID)
            if(spinnerBox != null)
                spinnerBox.setSelection(0)
        }

        this.currentObservation.clearAllValues()

    }

    public fun lecturingClick(view : View)
    {
        if(view is CheckBox) {
            val checkValue : Boolean = view.isChecked
            if(this.checkBoxLecturerIdentifiers.containsKey(view.id))
            {
                this.currentObservation.setLecturerValue(this.checkBoxLecturerIdentifiers[view.id] ?: "",checkValue)
            }
            else if (this.checkBoxStudentIdentifiers.containsKey(view.id))
            {
                this.currentObservation.setStudentValue(this.checkBoxStudentIdentifiers[view.id] ?: "",checkValue)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    public fun clickCOPUSurl(view: View)
    {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.COPUS_URL)))
        startActivity(browserIntent)
    }

    override fun onDeleteObservationAndProceed(dialog: DialogFragment?)
    {
        val view = findViewById<Button>(R.id.button)
        val counter: Chronometer = findViewById<Chronometer>(R.id.TimeView)
        this.pastObservations.clear()
        clearAllCheckboxes()
        view.text = getString(R.string.Timer_End)
        this.currentObservation.runTimer(10)
        counter.format = getString(R.string.Time_Stamp_Label)
        counter.base = SystemClock.elapsedRealtime()
        this.currentObservation.startTime = counter.base
        counter.start()
        //counter.onChronometerTickListener = object: TimerReact
        counter.setOnChronometerTickListener(TimerReact(this))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    override fun onDeleteObservationCanel(dialog : DialogFragment?)
    {
        // User clicked on the cancel button. Haco nada
    }

    override fun onStopTimerNoticeCancel(dialog : DialogFragment?)
    {
        // user clicked on the cancel button on the end timer button.
    }

    public fun startButton(view: View)
    {
        if(view is Button) {
            val counter: Chronometer = findViewById(R.id.TimeView)
            if (view.text == getString(R.string.Timer_Start))
            {
                if(this.pastObservations.size > 0)
                {
                    val confirmDelete = ConfirmDeleteCurrentObservation()
                    val fragmentManager = supportFragmentManager
                    confirmDelete.show(fragmentManager, "deleteCurrentObservation")
                }
                else
                {
                    onDeleteObservationAndProceed(null)
                }

            }
            else
            {
                pushCurrentState()
                view.text = getString(R.string.Timer_Start)
                this.currentObservation.stopTimer(10)
                counter.stop()
                clearAllCheckboxes()
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                val stopTimerNotice = StopTimerDialog()
                val fragmentManager = supportFragmentManager
                stopTimerNotice.show(fragmentManager,"noticeTimerEnd")
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    public fun sendResults(view: View)
    {

        var allObservations : String = this.currentObservation.headerToString() + "\n"
        var period : Int = 0
        for(pastObs in this.pastObservations)
        {
            period += 1
            allObservations += pastObs.convertToString(period) + "\n"
        }

        val fileToWrite : File? = saveStringAsFile(allObservations)
        if(fileToWrite!=null)
        {
            val settings  = PreferenceManager.getDefaultSharedPreferences(this)
            val toAddress : String = settings.getString("defaultEmailTo","") ?: ""
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_SUBJECT, "COPUS Observation")
            email.putExtra(Intent.EXTRA_EMAIL,arrayOf(toAddress))
            email.putExtra(
                Intent.EXTRA_TEXT,
                "The attachement includes the results from the observation."
            )
            email.setType("message/rfc822");
            try {
                val contentUri: Uri = getUriForFile(
                    applicationContext,
                    "org.cyclismo.copus.fileprovider",
                    fileToWrite
                )
                grantUriPermission(
                    "org.cyclismo.copus.fileprovider",
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                email.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
            catch (e : IllegalArgumentException)
            {
                //println(e)
            }
        }

    }

    private fun saveStringAsFile(contents:String) : File
    {
        val directoryFile: File = File(filesDir,"observations")
        directoryFile.mkdirs()

        val fileToWrite : File = File(directoryFile,"copus.csv")
        val outputStream : FileOutputStream = FileOutputStream(fileToWrite)
        //outputStream = openFileOutput("copusTempFile", Context.MODE_PRIVATE)
        outputStream.write(contents.toByteArray(Charset.defaultCharset()),0,contents.length)
        outputStream.flush()
        outputStream.close()

        return(File(directoryFile,"copus.csv"))
    }

    public fun pushCurrentState()
    {
        this.pastObservations.add(this.currentObservation)
        this.currentObservation = PeriodicUpdate()
        clearAllCheckboxes()
    }


    /*
    // from https://developer.android.com/training/data-storage/shared/documents-files
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
     */

    public fun requestFile() {
        /**
         * When the user requests a file, send an Intent to the
         * server app.
         * files.
         */
        startActivityForResult(this.requestFileIntent, 0)
    }

    /*
     * from https://developer.android.com/training/secure-file-sharing/request-file
     * When the Activity of the app that hosts files sets a result and calls
    * finish(), this method is invoked. The returned Intent contains the
    * content URI of a selected file. The result code indicates if the
    * selection worked or not.
    */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, returnIntent: Intent?)
    {
        super.onActivityResult(requestCode,resultCode,returnIntent)
        // If the selection didn't work
        if (resultCode != Activity.RESULT_OK) {
            // Exit without doing anything else
            return
        }
        // Get the file's content URI from the incoming Intent
        returnIntent!!.data?.also { returnUri ->
            /*
             * Try to open the file for "read" access using the
             * returned URI. If the file isn't found, write to the
             * error log and return.
             */
            this.inputPFD = try {
                /*
                 * Get the content resolver instance for this context, and use it
                 * to get a ParcelFileDescriptor for the file.
                 */
                contentResolver.openFileDescriptor(returnUri, "r")!!
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                //Log.e("MainActivity", "File not found.")
                return
            }

            // Get a regular file descriptor for the file
            //val fd = inputPFD.fileDescriptor

        }
    }

}

