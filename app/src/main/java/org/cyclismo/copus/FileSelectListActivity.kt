package org.cyclismo.copus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.NavUtils
import androidx.appcompat.app.ActionBar
import android.view.MenuItem

import org.cyclismo.copus.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_fileselect_list.*
import kotlinx.android.synthetic.main.fileselect_list_content.view.*
import kotlinx.android.synthetic.main.fileselect_list.*
import java.io.File
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.chrono.ChronoLocalDate
import java.util.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [FileSelectDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class FileSelectListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fileselect_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        //fab.setOnClickListener { view ->
        //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show()
        //}
        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (fileselect_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(fileselect_list)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setupRecyclerView(recyclerView: RecyclerView)
    {
        val directoryFile: File = File(filesDir,"observations")
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, directoryFile.listFiles().sortedByDescending {it.lastModified()}.toTypedArray(), twoPane)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: FileSelectListActivity,
        private val values: Array<File>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as File
                if (twoPane) {
                    val fragment = FileSelectDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(FileSelectDetailFragment.ARG_ITEM_ID, item.absolutePath)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fileselect_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, FileSelectDetailActivity::class.java).apply {
                        putExtra(FileSelectDetailFragment.ARG_ITEM_ID, item.absolutePath)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fileselect_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            val theDateStamp = SimpleDateFormat("HH:mm yyyy/MM/dd  z ")
            val modificationDate = theDateStamp.format(item.lastModified())
            holder.idView.text = item.name
            holder.contentView.text = modificationDate.toString()

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
