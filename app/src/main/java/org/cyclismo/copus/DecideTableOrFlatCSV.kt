package org.cyclismo.copus

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

class DecideTableOrFlatCSV : DialogFragment()
{
    internal lateinit var listener : DecideTypeCSVFile

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.ask_flat_or_table_csv)
                .setPositiveButton(R.string.print_flat_csv,
                    DialogInterface.OnClickListener
                    { _ , _ -> //dialog, id ->
                        listener.onPrintFlatCSV(this)
                    })
                .setNegativeButton(R.string.print_table_csv,
                    DialogInterface.OnClickListener
                    { _ , _ -> //dialog, id ->
                        listener.onPrintTableCSV(this)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try
        {
            listener = context as DecideTypeCSVFile
        }
        catch (e: ClassCastException)
        {
            throw ClassCastException(context.toString()+" Must implement DeleteNoticeDialogListener")
        }
    }


    interface DecideTypeCSVFile
    {
        abstract fun onPrintTableCSV(dialog: DialogFragment?)
        abstract fun onPrintFlatCSV(dialog : DialogFragment?)
    }
}