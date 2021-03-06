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

    This class is used to display a list of options for what format to use
    when saving the file. The options are to save as a flat file or as a table.

    ***************************************************************************  */

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