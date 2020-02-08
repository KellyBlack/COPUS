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

    This class is used to keep track of the portion of the view that displays
    the options that the user choses. It is a fragment that is displayed in
    the view of the main activity.

    This fragment includes all of the checkboxes and drop down options.
    When someone changes one of the checkboxes or drop downs this fragment
    has to record the result.

    ***************************************************************************  */
package org.cyclismo.copus

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import android.widget.Spinner as Spinner


class ClassActions : Fragment(),
    AdapterView.OnItemSelectedListener,
    View.OnClickListener
{

    // List of possible file formats. Used to share which option
    // the user chose.
    public enum class FileType
    {
        FLAT,
        TABLE
    }

    // The currentObservation records the current state of the view that the user
    // can see. The list of pastObservations is the set of all objects that record
    // what the user chose in the previous intervals.
    private var currentObservation : PeriodicUpdate = PeriodicUpdate()
    private var pastObservations : MutableList<PeriodicUpdate> = mutableListOf<PeriodicUpdate>()

    // Keep track of the view's IDs of the checkboxes and the spinnerboxes. This is used to
    // avoid repeated calling of the findViewID routine.
    private var checkBoxIDs = arrayOf<Int>()
    private var spinnerBoxIds = arrayOf<Int>()

    // A set of maps that can return the id of an object given the view.
    private var checkBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()
    private var spinnerBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()

    // A set of text labels that are used so that the id of a view can be used to determine
    // the label to print when requesting the current state or a past state.
    private var checkBoxLecturerIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    private var checkBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    private var spinnerBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()

    // The view of the fragment associated with this class.
    var localView : View? = null


    companion object {
        fun newInstance() = ClassActions()
    }

    private lateinit var viewModel: ClassActionsViewModel

    override fun onCreateView(
        // Function to take the xml file defining this view and expanding it out accordingly.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.localView = inflater.inflate(R.layout.class_actions_fragment, container, false)
        return this.localView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // Insure that the providers associated with this fragment are correctly set up.
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ClassActionsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        // At this point the view for this fragment has been created, and it is up
        // and running. Go though and figure out the IDs of all the views that are
        // being tracked by this object.
        super.onViewCreated(view, savedInstanceState)

        // Initialize the local variables. Save the id of this view, clear
        // the variables being tracked, and make sure the options are all correct.
        this.localView = view
        this.currentObservation.clearAllValues()
        clearAllCheckboxes()

        // Make a list of the IDs of all the views being tracked.
        this.checkBoxIDs = arrayOf(
            R.id.lecturingCheckbox,
            R.id.rtwCheckbox,
            R.id.fupCheckbox,
            R.id.pqCheckbox,
            R.id.cqCheckbox,
            R.id.anqCheckbox,
            R.id.mgCheckbox,
            R.id.oneOnOneCheckbox,
            R.id.dvCheckbox,
            R.id.adminCheckbox,
            R.id.waitingCheckbox,
            R.id.otherCheckbox,
            R.id.studentListeningCheckbox,
            R.id.individualCheckbox,
            R.id.cgCheckbox,
            R.id.wgCheckbox,
            R.id.ogCheckbox,
            R.id.studentAnswerCheckbox,
            R.id.sqCheckbox,
            R.id.wcCheckbox,
            R.id.prdCheckbox,
            R.id.spCheckbox,
            R.id.tqCheckbox,
            R.id.studentwaitingCheckbox,
            R.id.studentotherCheckbox
        )

        this.spinnerBoxIds = arrayOf(
            R.id.ListeningSpinner,
            R.id.ThinkingSpinnerind,
            R.id.CGSpinner,
            R.id.groupWorkSpinner,
            R.id.ogSpinner,
            R.id.ANQSpinner,
            R.id.SQSpinner,
            R.id.WCspinner,
            R.id.PRDspinner,
            R.id.SPSpinner,
            R.id.TQSpinner,
            R.id.waitingSpinner,
            R.id.otherSpinner
        )

        // Figure out the text label associated with each checkbox for when
        // the information is printed to a string that will be saved to a file.
        // This is for the checkboxes associated with the lecturer.
        this.checkBoxLecturerIdentifiers = mutableMapOf<Int,String>(
            R.id.lecturingCheckbox to "Lec",
            R.id.rtwCheckbox  to "RtW",
            R.id.fupCheckbox to "FUp",
            R.id.pqCheckbox to "PQ",
            R.id.cqCheckbox to "CQ",
            R.id.anqCheckbox to "AnQ",
            R.id.mgCheckbox to "MG",
            R.id.oneOnOneCheckbox to "1o1",
            R.id.dvCheckbox to "DV",
            R.id.adminCheckbox to "ADM",
            R.id.waitingCheckbox to "W",
            R.id.otherCheckbox to "O"
        )

        // Figure out the text label associated with each checkbox for when
        // the information is printed to a string that will be saved to a file.
        // This is for the checkboxes associated with the students.
        this.checkBoxStudentIdentifiers = mutableMapOf<Int,String>(
            R.id.studentListeningCheckbox to "L",
            R.id.individualCheckbox to "Ind",
            R.id.cgCheckbox to "CG",
            R.id.wgCheckbox to "WG",
            R.id.ogCheckbox to "OG",
            R.id.studentAnswerCheckbox to "AnQ",
            R.id.sqCheckbox to "SQ",
            R.id.wcCheckbox to "WC",
            R.id.prdCheckbox to "Prd",
            R.id.spCheckbox to "SP",
            R.id.tqCheckbox to "TQ",
            R.id.studentwaitingCheckbox to "W",
            R.id.studentotherCheckbox to "O"
        )

        // Figure out the text label associated with each spinnerbox for when
        // the information is printed to a string that will be saved to a file.
        // This is for the student engagment options for each student checkbox.
        this.spinnerBoxStudentIdentifiers = mutableMapOf<Int,String>(
            R.id.ListeningSpinner to "L",
            R.id.ThinkingSpinnerind to "Ind",
            R.id.CGSpinner to "CG",
            R.id.groupWorkSpinner to "WG",
            R.id.ogSpinner to "OG",
            R.id.ANQSpinner to "AnQ",
            R.id.SQSpinner to "SQ",
            R.id.WCspinner to "WC",
            R.id.PRDspinner to "Prd",
            R.id.SPSpinner to "SP",
            R.id.TQSpinner to "TQ",
            R.id.waitingSpinner to "W",
            R.id.otherSpinner to "O"
        )

        // Go through each checkbox and specify which function should be called
        // when someone clicks on it.
        for(checkBoxID in this.checkBoxIDs)
        {
            val checkboxView : CheckBox =  view.findViewById<CheckBox>(checkBoxID)
            checkboxView.setOnClickListener(this)
            this.checkBoxViews.put(checkboxView,checkBoxID)
        }

        // Ditto for the spinner boxes.
        for(spinnerBoxID in this.spinnerBoxIds)
        {
            val spinnerView : Spinner = view.findViewById<Spinner>(spinnerBoxID)
            this.spinnerBoxViews.put(spinnerView,spinnerBoxID)

            spinnerView.onItemSelectedListener = this@ClassActions
        }

    }

    override fun onNothingSelected(parent: AdapterView<out Adapter>?)
    {
        // Someone deselected all the options for a spinner box.
        // Clear the option in the current state
        this.currentObservation.setEngagementValue(this.spinnerBoxStudentIdentifiers[parent?.id] ?: "","")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long)
    {
        // Someone selected an option for one of the spinner boxes.
        // Set the option in the current state.
        val theSpinner = parent as Spinner
        this.currentObservation.setEngagementValue(this.spinnerBoxStudentIdentifiers[parent.id] ?: "",theSpinner.getSelectedItem().toString())
    }

    public fun numberObservations() : Int
    {
        // Called by a parent object to determine how many observations have been recorded.
        return(this.pastObservations.size)
    }

    public fun pushCurrentState()
    {
        // Take the current set of observations and save them on the history stack.
        // Clear the view so that we can start from scratch.
        this.pastObservations.add(this.currentObservation)
        this.currentObservation = PeriodicUpdate()
        clearAllCheckboxes()
    }

    public fun startNewObservation()
    {
        // Looks like we are about to start a new set of observations. Clear
        // the view and reset the past observations.
        this.pastObservations.clear()
        clearAllCheckboxes()
    }

    public fun clearAllCheckboxes()
    {
        // Reset the view so that nothing is checked and the spinner boxes are blanked out.

        // First blank out the checkboxes.
        for(checkboxID in this.checkBoxIDs)
        {
            if(this.localView!= null)
            {
                val checkbox: CheckBox = this.localView!!.findViewById<CheckBox>(checkboxID)
                checkbox.isChecked = false
            }
        }

        // Next blank out the spinner boxes.
        for(spinnerBoxID in this.spinnerBoxIds)
        {
            if(this.localView!= null)
            {
                try {
                    val spinnerBox: Spinner? = this.localView?.findViewById<Spinner>(spinnerBoxID)
                    if(spinnerBox!=null)
                        spinnerBox.setSelection(0)
                }
                catch ( e: NullPointerException)
                {
                    continue
                }
            }
        }

        // Finally reset the set of current observations.
        this.currentObservation.clearAllValues()

    }


    override fun onClick(view : View)
    {
        // One of the checkboxes was clicked. Determine if it was for a student or an instructor.
        // Record the value in the current state.
        if(view is CheckBox)
        {
            val checkValue : Boolean = view.isChecked

            if(this.checkBoxLecturerIdentifiers.containsKey(view.id))
            {
                // This is a lecturer checkbox. Set the current value for this.
                this.currentObservation.setLecturerValue(this.checkBoxLecturerIdentifiers[view.id] ?: "",checkValue)
            }
            else if (this.checkBoxStudentIdentifiers.containsKey(view.id))
            {
                // This is a student checkbox. Set the current value for this.
                this.currentObservation.setStudentValue(this.checkBoxStudentIdentifiers[view.id] ?: "",checkValue)
            }
        }
    }

    public fun observationsAsString(which : FileType) : String
    {
        // Someone has requested that the current set of observations be converted into a string and
        // saved in an appropriate file format.

        var period : Int = 0
        var allObservations: String = ""

        if(which == FileType.FLAT)
        {
            // They want a flat file. Go though the observations for each period and
            // convert each one to an appropriate string.
            // Append all the results together.
            allObservations = this.currentObservation.flatTableHeader() + "\n"
            for (pastObs in this.pastObservations)
            {
                period += 1
                allObservations += pastObs.convertToFlatString(period)
            }
        }
        else
        {
            // They want the data as a table. Go though the observations for each period and
            // convert each one to an appropriate string.
            // Append all the results together.
            allObservations = this.currentObservation.headerToString() + "\n"
            for (pastObs in this.pastObservations) {
                period += 1
                allObservations += pastObs.convertToString(period) + "\n"
            }
        }
        return(allObservations)
    }

    public fun isClear() : Boolean
    {
        // The parent object wants to know if the current state has any options checked off.
        return(this.currentObservation.isClear())
    }


}
