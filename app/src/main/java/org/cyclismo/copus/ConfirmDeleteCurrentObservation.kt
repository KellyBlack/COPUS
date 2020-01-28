package org.cyclismo.copus

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class ConfirmDeleteCurrentObservation : DialogFragment()
{
    internal lateinit var listener : DeleteNoticeDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.ask_delete_current_observation)
                .setPositiveButton(R.string.delete_current_observation,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDeleteObservationAndProceed(this)
                    })
                .setNegativeButton(R.string.cancel_delete_observation,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDeleteObservationCanel(this)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try
        {
            listener = context as DeleteNoticeDialogListener
        }
        catch (e: ClassCastException)
        {
            throw ClassCastException(context.toString()+" Must implement DeleteNoticeDialogListener")
        }
    }


    interface DeleteNoticeDialogListener
    {
        abstract fun onDeleteObservationAndProceed(dialog: DialogFragment?)
        abstract fun onDeleteObservationCanel(dialog : DialogFragment?)
    }
}