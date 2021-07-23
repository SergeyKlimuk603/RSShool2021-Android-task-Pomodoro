package by.klimuk.rsshool2021_android_task_pomodoro.models

import android.os.Handler
import android.util.Log

data class Timer(
    val timerListener: TimerListener,
    val id: Int,
    val setTime: Long,
    var timeLeft: Long = setTime,
    private var lastStartTimeLeft: Long = setTime,
    private var lastStartSystemTime: Long = -1L,
    var isRunning: Boolean = false,
): Runnable {

    private val handler = Handler()

    fun start() {
        if (timeLeft == 0L && !isRunning) {
            timeLeft = setTime
        }
        isRunning = true
        lastStartTimeLeft = timeLeft
        lastStartSystemTime = System.currentTimeMillis()
        handler.post(this)

    }

    fun stop() {
        isRunning = false
    }

    override fun run() {
        if (isRunning) {
            val oldTime = timeLeft / 1000
            val delta = System.currentTimeMillis() - this.lastStartSystemTime
            timeLeft = lastStartTimeLeft - delta
            if (oldTime != timeLeft / 1000) {
                //Log.d("TAG", "timeLeft = $timeLeft")
                timerListener.updateTime(this)
            }
            if (timeLeft <= 0) {
                timeLeft = 0
                isRunning = false
                timerListener.updateTime(this)
                timerListener.finish(this)
                return
            }
            handler.postDelayed(this, 100)
        }
    }

    interface TimerListener {
        fun updateTime(timer: Timer)
        fun finish(timer: Timer)
    }
}
