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

    private var currentObservation : PeriodicUpdate = PeriodicUpdate()
    private var pastObservations : MutableList<PeriodicUpdate> = mutableListOf<PeriodicUpdate>()
    private var checkBoxIDs = arrayOf<Int>()
    private var spinnerBoxIds = arrayOf<Int>()

    private var checkBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()
    private var spinnerBoxViews : MutableMap<View,Int> = mutableMapOf<View,Int>()

    private var checkBoxLecturerIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    private var checkBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()
    private var spinnerBoxStudentIdentifiers : MutableMap<Int,String> = mutableMapOf<Int,String>()

    var localView : View? = null


    companion object {
        fun newInstance() = ClassActions()
    }

    private lateinit var viewModel: ClassActionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.localView = inflater.inflate(R.layout.class_actions_fragment, container, false)
        return this.localView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ClassActionsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.localView = view
        this.currentObservation.clearAllValues()
        clearAllCheckboxes()

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

        for(checkBoxID in this.checkBoxIDs)
        {
            val checkboxView : CheckBox =  view.findViewById<CheckBox>(checkBoxID)
            checkboxView.setOnClickListener(this)
            this.checkBoxViews.put(checkboxView,checkBoxID)
        }

        for(spinnerBoxID in this.spinnerBoxIds)
        {
            val spinnerView : Spinner = view.findViewById<Spinner>(spinnerBoxID)
            this.spinnerBoxViews.put(spinnerView,spinnerBoxID)

            spinnerView.onItemSelectedListener = this@ClassActions
        }

    }

    override fun onNothingSelected(parent: AdapterView<out Adapter>?)
    {
        this.currentObservation.setEngagementValue(this.spinnerBoxStudentIdentifiers[parent?.id] ?: "","")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long)
    {
        val theSpinner = parent as Spinner
        this.currentObservation.setEngagementValue(this.spinnerBoxStudentIdentifiers[parent.id] ?: "",theSpinner.getSelectedItem().toString())
    }

    public fun numberObservations() : Int
    {
        return(this.pastObservations.size)
    }

    public fun pushCurrentState()
    {
        this.pastObservations.add(this.currentObservation)
        this.currentObservation = PeriodicUpdate()
        clearAllCheckboxes()
    }

    public fun startNewObservation()
    {
        this.pastObservations.clear()
        clearAllCheckboxes()
    }

    public fun clearAllCheckboxes()
    {

        for(checkboxID in this.checkBoxIDs)
        {
            if(this.localView!= null)
            {
                val checkbox: CheckBox = this.localView!!.findViewById<CheckBox>(checkboxID)
                if (checkbox != null)
                    checkbox.isChecked = false
            }
        }

        for(spinnerBoxID in this.spinnerBoxIds)
        {
            if(this.localView!= null)
            {
                val spinnerBox: Spinner? = this.localView!!.findViewById<Spinner>(spinnerBoxID)
                if (spinnerBox != null)
                    spinnerBox.setSelection(0)
            }
        }

        this.currentObservation.clearAllValues()

    }
    override fun onClick(view : View)
    {
        if(view is CheckBox)
        {
            val checkValue : Boolean = view.isChecked

            if(this.checkBoxLecturerIdentifiers.containsKey(view.id))
            {
                this.currentObservation.setLecturerValue(this.checkBoxLecturerIdentifiers[view.id] ?: "",checkValue)
            }
            else if (this.checkBoxStudentIdentifiers.containsKey(view.id))
            {
                this.currentObservation.setStudentValue(this.checkBoxStudentIdentifiers[view.id] ?: "",checkValue)
            }
        }
    }

    public fun observationsAsString() : String
    {
        var period : Int = 0
        var allObservations : String = this.currentObservation.headerToString() + "\n"
        for(pastObs in this.pastObservations)
        {
            period += 1
            allObservations += pastObs.convertToString(period) + "\n"
        }
        return(allObservations)
    }

    public fun isClear() : Boolean
    {
        return(this.currentObservation.isClear())
    }


}
