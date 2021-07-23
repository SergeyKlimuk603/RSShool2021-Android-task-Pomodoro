package by.klimuk.rsshool2021_android_task_pomodoro.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.util.TypedValue
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.klimuk.rsshool2021_android_task_pomodoro.R
import by.klimuk.rsshool2021_android_task_pomodoro.databinding.TimerItemBinding
import by.klimuk.rsshool2021_android_task_pomodoro.displayTime
import by.klimuk.rsshool2021_android_task_pomodoro.interfaces.TimerListener
import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer


class TimerViewHolder(
    private val binding: TimerItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val TWO_PI = 360F
    }

    private val timerListener = context as TimerListener

    fun bind(timer: Timer) {

        binding.tvLeftTime.text = (timer.timeLeft / 1000).displayTime()
        val angle = TWO_PI * (timer.timeLeft / 1000) / (timer.setTime / 1000)
        binding.timerView.setAngle(angle)
        initButtonListener(timer)
        if (timer.isRunning) {
            binding.btnStart.text = "Stop"
            binding.ivBlinking.isInvisible = timer.timeLeft / 1000 % 2 > 0
            //(binding.ivBlinking.background as? AnimationDrawable)?.start()
        } else {
            binding.ivBlinking.isInvisible = true
            binding.btnStart.text = "Start"
        }

        val typeValue = TypedValue()
        val timerBackgroundId = if (timer.timeLeft == 0L) {
            binding.tvLeftTime.text = (timer.setTime / 1000).displayTime()
            R.attr.colorPrimaryVariant
        } else {
            R.attr.colorBackgroundFloating
        }
        context.theme.resolveAttribute(timerBackgroundId, typeValue, true)

        val timerBackground = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(typeValue.resourceId, context.theme)
        } else {
            context.resources.getColor(typeValue.resourceId)
        }
        binding.clTimerItem.setBackgroundColor(timerBackground)
    }

    private fun initButtonListener(timer: Timer) {
        binding.btnDelete.setOnClickListener() {
            timerListener.delete(timer)
        }
        binding.btnStart.setOnClickListener() {
            if (timer.isRunning) {
                binding.btnStart.text = "Start"
                timerListener.stop(timer)
            } else {
                binding.btnStart.text = "Stop"
                timerListener.start(timer)
            }
        }
    }
}