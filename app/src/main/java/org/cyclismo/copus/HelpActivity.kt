package org.cyclismo.copus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat

//import androidx.appcompat.app.ActionBar

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val toolBar = supportActionBar //findViewById<Toolbar>(R.id.toolbar)
        if(toolBar != null)
        {
            toolBar.setDisplayHomeAsUpEnabled(true)
        }

        val myHelpText =  findViewById<TextView>(R.id.helpText)
        if(myHelpText != null)
        {
            val helpMessage = HtmlCompat.fromHtml(getString(R.string.help_page),HtmlCompat.FROM_HTML_MODE_COMPACT)
            myHelpText.setText(helpMessage.toString())
        }
    }


    override public fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
        {
            //NavUtils.navigateUpFromSameTask(this)
            onBackPressed()
            return(true)
        }
        return super.onOptionsItemSelected(item)
    }
}
