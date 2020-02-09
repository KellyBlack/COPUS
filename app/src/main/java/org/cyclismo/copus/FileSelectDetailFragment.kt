package org.cyclismo.copus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_fileselect_detail.*
import kotlinx.android.synthetic.main.fileselect_detail.view.*
import java.io.File
import java.lang.NullPointerException

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

                val directoryFile: File = File(directory,"observations")
                baseName = fileName.replace(directoryFile.absolutePath, "").replace(Regex("^/"),"")
                activity?.toolbar_layout?.title = baseName
                //val directoryFile: File = File(Context.getFilesDir(),"observations")
                //this.item = File(directoryFile,it.get(ARG_ITEM_ID))
                fileInfo = File(fileName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fileselect_detail, container, false)

        // Show the dummy content as text in a TextView.
        //item?.let {
            rootView.fileselect_detail.text = fileInfo.length().toString() //it.details
        //}

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
