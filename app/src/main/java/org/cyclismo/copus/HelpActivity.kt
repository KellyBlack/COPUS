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

/* ***************************************************************************

    This class is used to display the help view.

    ***************************************************************************  */

package org.cyclismo.copus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
            myHelpText.movementMethod = ScrollingMovementMethod()
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
