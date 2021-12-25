package org.savit.savitauthenticator.utils.otp

import android.os.Handler
import android.os.Looper
import org.savit.savitauthenticator.utils.TotpCounter

class TotpCountdownTask : Runnable {

    private var mCounter: TotpCounter? = null
    private var mRemainingTimeNotificationPeriod: Long = 0
    private val mHandler = Handler(Looper.getMainLooper())

    private var mLastSeenCounterValue = Long.MIN_VALUE
    private var mShouldStop = false


   var mListener: CountDownListener? = null

    constructor(
        counter: TotpCounter,
        remainingTimeNotificationPeriod: Long
    ) {
        mCounter = counter
        mRemainingTimeNotificationPeriod = remainingTimeNotificationPeriod
    }



    fun startAndNotifyListener() {
        if(mShouldStop){
            throw IllegalStateException("Task already stopped and cannot be restarted.")
        }else{
            run()
        }
    }

    fun start(){
        run()
    }

    fun stop() {
        mShouldStop = true
    }

    private fun getTimeTillNextCounterValue(time: Long): Long {
        val currentValue: Long = getCounterValue(time)
        val nextValue = currentValue + 1
        val nextValueStartTime: Long =
            secondsToMillis(mCounter!!.getValueStartTime(nextValue))
        return nextValueStartTime - time
    }

    fun setListener(listener: CountDownListener?) {
        mListener = listener
    }


    private fun getCounterValue(time: Long): Long {
        return mCounter!!.getValueAtTime(millisToSeconds(time))
    }

    private fun getCounterValueAge(time: Long): Long {
        return time - secondsToMillis(mCounter!!.getValueStartTime(getCounterValue(time)))
    }


    private fun scheduleNextInvocation() {
        val now: Long = System.currentTimeMillis()
        val counterValueAge: Long = getCounterValueAge(now)
        val timeTillNextInvocation =
            mRemainingTimeNotificationPeriod - counterValueAge % mRemainingTimeNotificationPeriod
        mHandler.postDelayed(this, timeTillNextInvocation)
    }

    private fun fireTotpCountdown(timeRemaining: Long) {
        if (mListener != null && !mShouldStop) {
            mListener!!.onTotpCountdown(timeRemaining)
        }
    }

    private fun fireTotpCounterValueChanged() {
        if (mListener != null && !mShouldStop) {
            mListener!!.onTotpCounterValueChanged()
        }
    }

    override fun run() {
        if (mShouldStop) {
            return
        }

        val now: Long = System.currentTimeMillis()
        val counterValue = getCounterValue(now)
        if (mLastSeenCounterValue != counterValue) {
            mLastSeenCounterValue = counterValue
            fireTotpCounterValueChanged()
        }
        fireTotpCountdown(getTimeTillNextCounterValue(now))

        scheduleNextInvocation()
    }


    private fun secondsToMillis(timeSeconds: Long): Long {
        return timeSeconds * 1000
    }

    fun millisToSeconds(timeMillis: Long): Long {
        return timeMillis / 1000
    }


}