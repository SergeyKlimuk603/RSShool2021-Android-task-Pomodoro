package by.klimuk.rsshool2021_android_task_pomodoro.models

data class Timer(
    val timerListener: Timer.TimerListener,
    val id: Int,
    val setTime: Long,
    var currentTime: Long = setTime,
    var lastStartCurrentTime: Long = setTime,
    var lastStartSystemTime: Long = -1L,
    var isRunning: Boolean = false,
) {
    fun updateTime(currentSystemTime: Long) {
        val oldTime = currentTime / 1000
        val delta = currentSystemTime - this.lastStartSystemTime
        currentTime = lastStartCurrentTime - delta
        if (oldTime != currentTime / 1000) {
            timerListener.updateTime(this)
        }
        if (currentTime <= 0) {
            currentTime = 0
            isRunning = false
            timerListener.finish(this)
        }
    }

    fun start(currentSystemTime: Long) {
        isRunning = true
        lastStartCurrentTime = currentTime
        lastStartSystemTime = currentSystemTime

    }

    fun stop(currentSystemTime: Long) {
        isRunning = false
        //updateTime(currentSystemTime) только мешает
    }

    interface TimerListener {
        fun updateTime(timer: Timer)
        fun finish(timer: Timer)
    }
}
