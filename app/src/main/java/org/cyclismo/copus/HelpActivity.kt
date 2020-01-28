package org.cyclismo.copus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View

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
