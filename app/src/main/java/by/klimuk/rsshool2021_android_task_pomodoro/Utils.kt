package by.klimuk.rsshool2021_android_task_pomodoro

const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"
const val COMMAND_ID = "COMMAND_ID"
const val TIME_LEFT_MS = "TIME_LEFT_MS"
const val START_SERVICE_TIME = "START_SERVICE_TIME"

// Функция преобразует время в удобный для человека формат
fun Long.displayTime(): String {
    val h = this / 3600
    val m = this % 3600 / 60
    val s = this % 60
    return String.format("%02d:%02d:%02d", h, m, s);
}