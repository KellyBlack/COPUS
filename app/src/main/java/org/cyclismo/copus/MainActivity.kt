/*

    Copyright 2020, Kelly Black
    Department of Mathematics
    University of Georgia
    Athens GA 30602

    This file is part of the COPUS Android app.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

package org.cyclismo.copus

//import com.google.android.material.snackbar.Snackbar

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
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
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit




class MainActivity : AppCompatActivity(),
    ConfirmDeleteCurrentObservation.DeleteNoticeDialogListener,
    StopTimerDialog.StopNoticeDialogListener,
    //Runnable,
    DecideTableOrFlatCSV.DecideTypeCSVFile

{

    private var startButton : Button? = null
    private lateinit var requestFileIntent: Intent
    private lateinit var inputPFD: ParcelFileDescriptor

    private var timerRunning : Boolean = false
    private var baseFileName : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _: View ->
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //    .setAction("Action", null).show()

            // This is for when someone clicks on the email button
            // on the lower part of the screen.
            decideFileType()
        }

        // Set the default file type. We usually share csv files.
        this.requestFileIntent = Intent(Intent.ACTION_PICK).apply {
            type = "text/csv"
        }

        // Set the value of the variable that holds the value of the start button
        this.startButton = findViewById<Button>(R.id.button)
        if(android.os.Build.VERSION.SDK_INT>android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                // If the OS allows, change the colour of the start button so it stands out a bit.
                val drawing = getDrawable(R.drawable.button_details) //21
                this.startButton!!.setBackground(drawing)
            } catch (e: RuntimeException) {

            }
        }

        //val exec = ScheduledThreadPoolExecutor(1)
        //exec.scheduleAtFixedRate(this,0,3,TimeUnit.SECONDS)

    }


/*
    override public fun run() {
        println("Hello world");
    }
*/


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
                // The user chose the option to change the settings.
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_fileSelect ->
            {
                // The user chose the file option to display the files.
                startActivity(Intent(this@MainActivity,FileSelectListActivity::class.java))
                true
            }
            R.id.action_about ->
            {
                // The user chose the option to display the "about" view
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                true
            }
            R.id.action_help ->
            {
                // The user chose the option to display the help text.
                startActivity(Intent(this@MainActivity,HelpActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item) // Nothing for me to do here....
        }
    }


    @Suppress("UNUSED_PARAMETER")
    public fun clickCOPUSurl(view: View)
    {
        // If someone clicks on the about screen open up the URL to the
        // COPUS web page so they can get more details.
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.COPUS_URL)))
        startActivity(browserIntent)
    }

    override fun onDeleteObservationAndProceed(dialog: DialogFragment?)
    {
        // Someone hit the start button and confirmed they want to delete the
        // unsaved observation. Start this bad boy up!
        // Change the button, reset any stray text, and start the timer.
        val view = findViewById<Button>(R.id.button)
        val textLabel = findViewById<TextView>(R.id.timerTextLabel)
        val counter: Chronometer = findViewById<Chronometer>(R.id.TimeView)
        val observationFragment = supportFragmentManager.findFragmentById(R.id.observationFragment) as ClassActions

        // Let the object keeping track of view to the current observation know we are
        // starting over. Make everything blank and change the start button.
        observationFragment.startNewObservation()
        view.text = getString(R.string.Timer_End)
        textLabel.text = ""
        counter.format = getString(R.string.Time_Stamp_Label)

        // The timer needs to be started.
        this.timerRunning = true

        // Set the base name for the file that will be created when the observation is saved.
        val theDateStamp = SimpleDateFormat("yyyy-MM-dd HH:mm z")
        this.baseFileName = determineFileBaseName()

        // The timer react object will keep track of the last time the timer rolled over to the next
        // two minute time, and the base is used to check how long that has happened. This needs to
        // be updated regularly. Then start this thing.
        counter.base = SystemClock.elapsedRealtime()
        counter.start()

        // Let the button know it is time to go.
        //counter.onChronometerTickListener = object: TimerReact
        counter.setOnChronometerTickListener(TimerReact(this))

        // Make sure the screen does not lock up in the middle of an observation or
        // just before making an observation.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }

    override fun onDeleteObservationCanel(dialog : DialogFragment?)
    {
        // User clicked on the cancel button. Nothing to do????
    }

    override fun onStopTimerNoticeCancel(dialog : DialogFragment?)
    {
        // user clicked on the cancel button on the end timer button. Nothing to do???
    }

    public fun pushCurrentState(period:Long = -1)
    {
        // The timer has rolled over to the next two minute period.
        // Save all the information, update the text labels, and
        // let the view to the menu options know to start over.

        val textLabel = findViewById<TextView>(R.id.timerTextLabel)
        val observationFragment = supportFragmentManager.findFragmentById(R.id.observationFragment) as ClassActions
        val periodText = "(${period})"
        observationFragment.pushCurrentState()
        if(period<0)
            textLabel.text = ""
        else
            textLabel.text = periodText
    }

    private fun determineFileBaseName(suffix:String="") : String
    {
        // Set the base name for the file that will be created when the observation is saved.
        val theDateStamp = SimpleDateFormat("yyyy-MM-dd HH:mm z")
        return("copus_${theDateStamp.format(Calendar.getInstance().time)}${suffix}")
    }

    public fun startButton(view: View)
    {
        /*
            This is the function that starts the main action. It is called whenever someone
            clicks on the start button. The state of the system is defined by the text on
            the start button. If it says "start" then it means the timer was off, and the
            user needs to confirm that she really wants to start the timer. Otherwise it
            is time to bring everything to a close.
         */

        if(view is Button)
        {
            // The thing pressed was a button. Find the timer view and the fragment that has the
            // information of the current set of observations.
            val counter: Chronometer = findViewById(R.id.TimeView)
            val observationFragment = supportFragmentManager.findFragmentById(R.id.observationFragment) as ClassActions

            if (view.text == getString(R.string.Timer_Start))
            {

                if(observationFragment.numberObservations() > 0)
                {
                    // We have an old set of data. Double check to make sure we really
                    // want to start this thing.
                    val confirmDelete = ConfirmDeleteCurrentObservation()
                    val fragmentManager = supportFragmentManager
                    confirmDelete.show(fragmentManager, "deleteCurrentObservation")
                }
                else
                {
                    // There is no old data. Just start it and get going.
                    onDeleteObservationAndProceed(null)
                }


            }
            else
            {
                // We need to stop the timer.
                this.timerRunning = false

                if(!observationFragment.isClear())
                {
                    // We have unsaved information. Save it to the list of observations.
                    observationFragment.pushCurrentState()
                }

                // Reset the buttons and labels and such. Clear the display and turn off
                // the flag that stops the screen saver from blanking the screen.
                view.text = getString(R.string.Timer_Start)
                counter.stop()
                observationFragment.clearAllCheckboxes()
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                // Stop the timer and let the user know that nothing has been saved yet.
                val stopTimerNotice = StopTimerDialog()
                val fragmentManager = supportFragmentManager
                stopTimerNotice.show(fragmentManager,"noticeTimerEnd")
            }
        }
    }

    override fun onPrintTableCSV(dialog: DialogFragment?)
    {
        // Called in response to the user hitting the email dialog.
        // The user wants to email the csv file as a table.
        sendResults(ClassActions.FileType.TABLE)
    }

    override fun onPrintFlatCSV(dialog : DialogFragment?)
    {
        // Called in response to the user hitting the email dialog.
        // The user wants to email the csv file as a flat file.
        sendResults(ClassActions.FileType.FLAT)
    }

    private fun decideFileType()
    {
        // The user hit the email button. We need to figure out whether the
        // person wants to send a table or a flat file. Put up a dialog with
        // the options.

        if(this.timerRunning)
        {
            // The timer is running. post a message saying to stop
            // the timer before trying to save.
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.apply {
                setPositiveButton(R.string.email_cannot_send_resume,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            }
            builder.setMessage(R.string.email_cannot_send_timer_on)
                .setTitle(resources.getString(R.string.email_cannot_send_title))
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        else if (this.baseFileName.length == 0)
        {
            // There is no current file. Put up a notice saying there is nothing to send.
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.apply {
                setPositiveButton(R.string.email_cannot_send_resume,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            }
            builder.setMessage(R.string.email_cannot_send_no_observation)
                .setTitle(resources.getString(R.string.email_cannot_send_title))
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
        else
        {
            val decideFileType = DecideTableOrFlatCSV()
            val fragmentManager = supportFragmentManager
            decideFileType.show(fragmentManager, "decideFileType")
        }
    }

    public fun sendResults(which : ClassActions.FileType)
    {

        // The user hit the email button and responded to the window asking what kind of file
        // they want to send. We just need to collect the data, create a file, and then
        // request that the email application pass the information along.

        // Get the fragment that keeps track of the user input. Save the
        // results in one string that has the information in the necessary
        // format.
        val observationFragment = supportFragmentManager.findFragmentById(R.id.observationFragment) as ClassActions
        val allObservations : String = observationFragment.observationsAsString(which)
        var fileToWrite : File?

        // Save the information to a file.
        try
        {
            fileToWrite = saveStringAsFile(allObservations)
        }
        catch(e:FileNotFoundException)
        {
            // TODO exit here more gracefully
            return
        }

        if(fileToWrite!=null)
        {
            // The file has been saved. Now call up the email application and
            // make sure it knows the file to include as an attachment.
            val settings  = PreferenceManager.getDefaultSharedPreferences(this)
            val toAddress : String = settings.getString("defaultEmailTo","") ?: ""
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_SUBJECT, "COPUS Observation")
            email.putExtra(Intent.EXTRA_EMAIL,arrayOf(toAddress))
            email.putExtra(
                Intent.EXTRA_TEXT,
                "The attachement includes the results from the observation."
            )
            email.setType("message/rfc822")
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
                email.putExtra(Intent.EXTRA_STREAM, contentUri)
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
            catch (e : IllegalArgumentException)
            {
                // oops! Could not get the file for some reason. Just walk away...
                // TODO - really should be more graceful about this, ya know!
                //println(e)
            }
        }

    }

    private fun saveStringAsFile(contents:String,suffix:String="") : File
    {
        // Take the set of observations as a string and save them to a file.
        // Return a File object that points to the file just created.
        val directoryFile: File = File(filesDir,"observations")
        directoryFile.mkdirs()

        val theDateStamp = SimpleDateFormat("yyyy-MM-dd HH:mm z")
        val fileName : String = "${determineFileBaseName()}.csv"
        var fileToWrite: File = File(directoryFile,fileName)
        val outputStream : FileOutputStream = FileOutputStream(fileToWrite)
        //outputStream = openFileOutput("copusTempFile", Context.MODE_PRIVATE)
        outputStream.write(contents.toByteArray(Charset.defaultCharset()),0,contents.length)
        outputStream.flush()
        outputStream.close()

        return(File(directoryFile,fileName))
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

