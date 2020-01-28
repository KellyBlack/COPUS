package org.cyclismo.copus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View

class AboutActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolBar = supportActionBar
        if(toolBar != null)
        {
            toolBar.setDisplayHomeAsUpEnabled(true)
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


    fun onTextViewClick(view: View)
    {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.COPUS_URL)))
        startActivity(browserIntent)
    }

}
