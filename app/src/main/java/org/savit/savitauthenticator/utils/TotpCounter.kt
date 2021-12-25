package org.savit.savitauthenticator.utils

class TotpCounter {

    private var mTimeStep: Long = 0
    private var mStartTime: Long = 0

    constructor(timeStep: Long):this(timeStep,0){

    }

    constructor(timeStep:Long,startTime:Long){
        require(timeStep >= 1) { "Time step must be positive: $timeStep" }
        assertValidTime(startTime)

        mTimeStep = timeStep
        mStartTime = startTime

    }

    fun getTimeStep(): Long {
        return mTimeStep
    }

    fun getStartTime(): Long {
        return mStartTime
    }

    companion object{
        private fun assertValidTime(time: Long) {
            require(time >= 0) { "Negative time: $time" }
        }
    }

    fun getValueStartTime(value: Long): Long {
        return mStartTime + value * mTimeStep
    }

    fun getValueAtTime(time: Long): Long {
        assertValidTime(time)

        // According to the RFC:
        // T = (Current Unix time - T0) / X, where the default floor function is used.
        //   T  - counter value,
        //   T0 - start time.
        //   X  - time step.

        // It's important to use a floor function instead of simple integer division. For example,
        // assuming a time step of 3:
        // Time since start time: -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6
        // Correct value:         -2 -2 -2 -1 -1 -1  0  0  0  1  1  1  2
        // Simple division / 3:   -2 -1 -1 -1  0  0  0  0  0  1  1  1  2
        //
        // To avoid using Math.floor which requires imprecise floating-point arithmetic,
        // we compute the value using integer division, but using a different equation for
        // negative and non-negative time since start time.
        val timeSinceStartTime = time - mStartTime
        return if (timeSinceStartTime >= 0) {
            timeSinceStartTime / mTimeStep
        } else {
            (timeSinceStartTime - (mTimeStep - 1)) / mTimeStep
        }
    }


}