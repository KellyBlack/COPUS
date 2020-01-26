package org.cyclismo.copus

import android.os.SystemClock
import android.widget.Chronometer

class TimerReact(private val parentActivity:MainActivity) : Chronometer.OnChronometerTickListener
{
    private var numberIntervals : Long = 1

    /*
    init
    {

    }
     */


    override fun onChronometerTick(chronometer: Chronometer?) {
        if(chronometer != null) {
            println("la hora esta las ${SystemClock.elapsedRealtime() - chronometer.base}")
            if (SystemClock.elapsedRealtime() - chronometer.base > 60000/4*numberIntervals)
            {
                numberIntervals += 1
                parentActivity.pushCurrentState()
            }
        }
    }

}