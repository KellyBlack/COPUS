package org.cyclismo.copus

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class StopTimerDialog : DialogFragment()
{
    internal lateinit var listener : StopTimerDialog.StopNoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.timer_stopped_notice)
                .setPositiveButton(R.string.timer_continue_button,
                    DialogInterface.OnClickListener { _, _ -> //dialog, id ->
                        listener.onStopTimerNoticeCancel(this)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try
        {
            listener = context as StopTimerDialog.StopNoticeDialogListener
        }
        catch (e: ClassCastException)
        {
            throw ClassCastException(context.toString()+" Must implement DeleteNoticeDialogListener")
        }
    }

    interface StopNoticeDialogListener
    {
        abstract fun onStopTimerNoticeCancel(dialog : DialogFragment?)
    }

}