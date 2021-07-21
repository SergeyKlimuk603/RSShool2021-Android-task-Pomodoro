package by.klimuk.rsshool2021_android_task_pomodoro.adapters

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import by.klimuk.rsshool2021_android_task_pomodoro.databinding.TimerItemBinding
import by.klimuk.rsshool2021_android_task_pomodoro.interfaces.TimerListener
import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer


class TimerViewHolder(
    private val binding: TimerItemBinding,
    private val timerListener: TimerListener
) : RecyclerView.ViewHolder(binding.root){

    companion object {
        const val TWO_PI = 360F
    }

    fun bind(timer: Timer) {

        binding.tvLeftTime.text = (timer.currentTime / 1000).toInt().displayTime()
        binding.timerView.setAngle(TWO_PI * timer.currentTime / timer.setTime)
        initButtonListener(timer)
        if (timer.isRunning) {
            binding.btnStart.text = "Stop"
            (binding.ivBlinking.background as? AnimationDrawable)?.start()
        } else {
            binding.btnStart.text = "Start"
        }
    }

    private fun initButtonListener(timer: Timer) {
        binding.btnDelete.setOnClickListener() {
            timerListener.delete(timer)
        }
        binding.btnStart.setOnClickListener() {
            if (timer.isRunning) {
                //timer.isRunning = false
                binding.btnStart.text = "Start"
                timerListener.stop(timer)
            } else {
                //timer.isRunning = true
                binding.btnStart.text = "Stop"
                timerListener.start(timer)
            }
        }
    }

    // Функция преобразует время в удобный для человека формат
    private fun Int.displayTime(): String {
        val h = this / 3600
        val m = this % 3600 / 60
        val s = this % 60
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}