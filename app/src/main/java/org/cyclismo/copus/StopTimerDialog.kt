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

    This class is used to display a notice when the user clicks the stop
    button and ends an observation.

    ***************************************************************************  */


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