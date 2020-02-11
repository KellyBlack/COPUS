package org.cyclismo.copus

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_fileselect_detail.*
import kotlinx.android.synthetic.main.fileselect_detail.view.*
import java.io.File
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import kotlin.NullPointerException

/**
 * A fragment representing a single FileSelect detail screen.
 * This fragment is either contained in a [FileSelectListActivity]
 * in two-pane mode (on tablets) or a [FileSelectDetailActivity]
 * on handsets.
 */
class FileSelectDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private lateinit var fileInfo: File
    private lateinit var fileName : String
    private lateinit var baseName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID))
            {
                var directory : File
                try {
                    directory = activity!!.filesDir
                    fileName = it.get(ARG_ITEM_ID).toString() ?: ""
                }
                catch (e:NullPointerException)
                {
                    return
                }

                var directoryFile: File? = null
                try {
                    directoryFile = getDirectory()
                }
                catch(e:NullPointerException)
                {

                }
                finally {
                    fileName = it.get(ARG_ITEM_ID).toString() ?: ""
                    baseName = fileName.replace(directoryFile!!.absolutePath, "").replace(Regex("^/"),"")
                    activity?.toolbar_layout?.title = baseName
                    //val directoryFile: File = File(Context.getFilesDir(),"observations")
                    //this.item = File(directoryFile,it.get(ARG_ITEM_ID))
                    fileInfo = File(fileName)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fileselect_detail, container, false)

        //item?.let { }

        var numberObservations : Int = 0
        if (fileInfo.exists()) {
            fileInfo.forEachLine {
                numberObservations += 1
                val columns : List<String> = it.split(",")
                if (columns.size > 0)
                {
                    try {
                        numberObservations = columns[0].toInt()
                    }
                    catch (e:NumberFormatException)
                    {

                    }

                }
            }
        }

        val theDateStamp = SimpleDateFormat("HH:mm yyyy/MM/dd  z ")
        val modificationDate = theDateStamp.format(fileInfo.lastModified())
        rootView.fileselect_detail.text = modificationDate.toString() + " (${numberObservations})"

        rootView.delete_file.setOnClickListener({ v: View -> delete_file(v)})
        rootView.mail_file.setOnClickListener({ v: View -> send_file(v)})
        rootView.rename_file.setOnClickListener({ v: View -> request_rename_file(v)})


        return rootView
    }



    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }

    fun getDirectory() : File?
    {

        var directory: File
        try {
            directory = activity!!.filesDir
        } catch (e: NullPointerException) {
            return(null)
        }

        val directoryFile: File = File(directory, "observations")
        return(directoryFile)
    }

    fun request_rename_file(view: View)
    {
        try {
            val builder: AlertDialog.Builder = activity!!.let { AlertDialog.Builder(it) }
            val inflater = requireActivity().layoutInflater;
            builder.apply {
                val view : View = inflater.inflate(R.layout.rename_fileselect_detail,null)
                val textEdit : EditText = view.findViewById<EditText>(R.id.editText)
                textEdit.setText(baseName)
                setView(view)
                setPositiveButton(R.string.fileselect_rename,
                    DialogInterface.OnClickListener { dialog : DialogInterface, id ->
                        try {
                            val alert: AlertDialog = dialog as AlertDialog
                            //val textEdit: EditText = alert!!.findViewById<EditText>(R.id.editText)
                            rename_file(textEdit.getText().toString())
                        }
                        catch(e:ClassCastException)
                        {

                        }
                        catch (e: NullPointerException)
                        {

                        }
                    })
                setNegativeButton(R.string.fileselect_cancel,
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            }
            val titlePrefix : String = resources.getString(R.string.fileselect_rename_title)
            builder.setMessage(R.string.fileselect_rename_message)
                .setTitle("${titlePrefix} $baseName")
            val dialog: AlertDialog = builder!!.create()
            dialog.show()
        }
        catch (e:NullPointerException)
        {

        }
    }

    fun rename_file(newFileName : String)
    {
        var directoryFile: File? = null
        try {
            directoryFile = getDirectory()
        }
        catch(e:NullPointerException)
        {
            return
        }

        var intendedFileName : String = ""
        try {
            intendedFileName = directoryFile!!.absolutePath + "/" + newFileName
        }
        catch (e:NullPointerException)
        {
            return
        }

        //val directoryFile: File = File(Context.getFilesDir(),"observations")
        //this.item = File(directoryFile,it.get(ARG_ITEM_ID))
        fileName = intendedFileName
        baseName = newFileName
        val newFile = File(fileName)
        fileInfo.renameTo(newFile)

        try {
            val parent : FileSelectDetailActivity = activity!! as FileSelectDetailActivity
            parent.changeTitle(baseName)
            //parent.toolbar_layout?.title = baseName
        }
        catch (e:java.lang.NullPointerException)
        {

        }

    }

    fun send_file(view: View)
    {
        if(fileInfo.exists())
        {
            // The file has been saved. Now call up the email application and
            // make sure it knows the file to include as an attachment.
            val settings  = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
            val toAddress : String = settings.getString("defaultEmailTo","") ?: ""
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_SUBJECT, "COPUS Observation")
            email.putExtra(Intent.EXTRA_EMAIL,arrayOf(toAddress))

            val theDateStamp = SimpleDateFormat("HH:mm yyyy/MM/dd  z ")
            val modificationDate = theDateStamp.format(fileInfo.lastModified())
            email.putExtra(
                Intent.EXTRA_TEXT,
                "The attachement includes the results from the observation on ${modificationDate.toString()}."
            )
            email.setType("message/rfc822")
            try {
                val context = activity!!.applicationContext
                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "org.cyclismo.copus.fileprovider",
                    fileInfo
                )
                context.grantUriPermission(
                    "org.cyclismo.copus.fileprovider",
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                email.putExtra(Intent.EXTRA_STREAM, contentUri)
                startActivity(Intent.createChooser(email, "Choose an Email client :"))
            }
            catch (e:NullPointerException)
            {

            }
            catch (e : IllegalArgumentException)
            {
                // oops! Could not get the file for some reason. Just walk away...
                // TODO - really should be more graceful about this, ya know!
                //println(e)
            }
        }
    }

    fun delete_file(view: View)
    {
        println("Delete file ${fileInfo.absolutePath}")

        try {
            val builder: AlertDialog.Builder = activity!!.let { AlertDialog.Builder(it) }
            builder.apply {
                setPositiveButton(R.string.fileselect_delete,
                    DialogInterface.OnClickListener { dialog, id ->
                        try {
                            fileInfo!!.delete()
                            activity!!.onBackPressed()
                        }
                        catch (e:NullPointerException)
                        {

                        }


                    })
                setNegativeButton(R.string.fileselect_cancel,
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            }
            builder.setMessage(R.string.fileselect_delete_message)
                .setTitle("${resources.getString(R.string.fileselect_delete_title)}  $baseName")
            val dialog: AlertDialog = builder!!.create()
            dialog.show()
        }
        catch (e:NullPointerException)
        {

        }
    }

}
