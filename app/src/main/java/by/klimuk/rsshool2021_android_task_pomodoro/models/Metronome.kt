package by.klimuk.rsshool2021_android_task_pomodoro.models

import android.os.Handler


class Metronome(private val listener: CallBack,
                private val period: Long) : Runnable{

    private val handler = Handler()
    private var isRunning = false
    private var isHandlerRunning = false

    override fun run() {
        isHandlerRunning = false
        if (isRunning) {
            listener.tick()
            handler.postDelayed(this, period)
            isHandlerRunning = true
        }
    }

    fun start() {
        if (isHandlerRunning) return
        if (!isRunning) {
            handler.post(this)
            isRunning = true
        }
    }

    fun stop() {
        isRunning = false
    }

    interface CallBack {
        fun tick()
    }
}