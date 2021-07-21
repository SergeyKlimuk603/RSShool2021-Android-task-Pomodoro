package by.klimuk.rsshool2021_android_task_pomodoro.interfaces

import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer

interface TimerListener {
    fun start(timer: Timer)
    fun stop(timer: Timer)
    fun delete(timer: Timer)
}