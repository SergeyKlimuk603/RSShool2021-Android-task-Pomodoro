package by.klimuk.rsshool2021_android_task_pomodoro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import by.klimuk.rsshool2021_android_task_pomodoro.databinding.TimerItemBinding
import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer

class TimerAdapter (
    private val context: Context
        ): ListAdapter<Timer, TimerViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TimerItemBinding.inflate(layoutInflater, parent, false)
        return TimerViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    private companion object {
        private val itemComparator = object : DiffUtil.ItemCallback<Timer>() {
            override fun areItemsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Timer, newItem: Timer): Boolean {
                return oldItem.timeLeft == newItem.timeLeft &&
                        oldItem.isRunning == newItem.isRunning
            }
        }
    }
}