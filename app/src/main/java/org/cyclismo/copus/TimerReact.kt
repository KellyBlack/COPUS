package org.cyclismo.copus
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


import android.os.SystemClock
import android.widget.Chronometer

class TimerReact(private val parentActivity:MainActivity) : Chronometer.OnChronometerTickListener
{
    val TESTING : Boolean = false

    private var numberIntervals : Long = 1

    /*
    init
    {

    }
     */


    override fun onChronometerTick(chronometer: Chronometer?) {
        if(chronometer != null) {
            var nextTimeCheck : Long = 120000*numberIntervals
            if(TESTING)
                nextTimeCheck = 8000*numberIntervals

            if (SystemClock.elapsedRealtime() - chronometer.base > nextTimeCheck)
            {
                numberIntervals += 1
                parentActivity.pushCurrentState(numberIntervals)
            }
        }
    }

}