package org.cyclismo.copus

import android.os.SystemClock
import android.widget.Chronometer

class TimerReact(private val parentActivity:MainActivity) : Chronometer.OnChronometerTickListener
{
    val TESTING : Boolean = true

    private var numberIntervals : Long = 1

    /*
    init
    {

    }
     */


    override fun onChronometerTick(chronometer: Chronometer?) {
        if(chronometer != null) {
            var nextTimeCheck : Int = 60000*numberIntervals
            if(TESTING)
                nextTimeCheck = 60000/4*numberIntervals

            if (SystemClock.elapsedRealtime() - chronometer.base > nextTimeCheck)
            {
                numberIntervals += 1
                parentActivity.pushCurrentState()
            }
        }
    }

}