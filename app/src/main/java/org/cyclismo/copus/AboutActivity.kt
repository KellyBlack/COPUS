package org.cyclismo.copus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AboutActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    fun onButtonClick(view: View)
    {
        onBackPressed()
    }

    fun onTextViewClick(view: View)
    {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.COPUS_URL)))
        startActivity(browserIntent)
    }

}
