package by.klimuk.rsshool2021_android_task_pomodoro

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.klimuk.rsshool2021_android_task_pomodoro.adapters.TimerAdapter
import by.klimuk.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import by.klimuk.rsshool2021_android_task_pomodoro.interfaces.TimerListener
import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer


class MainActivity : AppCompatActivity(), TimerListener, Timer.TimerListener {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimerAdapter(this)
    private val timers = mutableListOf<Timer>()
    private var nextId = 0
    private var runningTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTimers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = timerAdapter
        }

        binding.btnAddTimer.setOnClickListener {
            val timerSet = binding.etEnterTime.text.toString().toIntOrNull() ?: -1
            if (timerSet in 1..5999) {
                timers.add(Timer(this, nextId++, timerSet * 60000L))
                timerAdapter.submitList(timers.toList())
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0);
            } else {
                val toast = Toast.makeText(this,
                    "Значение уставки должно быть в пределах от 1 до 5999 минут",
                    Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0 , 0)
                toast.show()
            }
        }
    }

    // Переопределенные функции интерфейсов
    override fun updateTime(timer: Timer) {
        Log.d("TAG", "MainActivity updateTime()---------------")
        timerAdapter.notifyDataSetChanged()
    }
    override fun finish(timer: Timer) {
        val toast = Toast.makeText(this,
            "Время вышло!!!",
            Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0 , 0)
        toast.show()
        Log.d("TAG", "MainActivity finish()---------------")

    }

    override fun start(timer: Timer) {
        Log.d("TAG", "MainActivity start() startTimerId = ${timer.id}")
        if (runningTimer != null) {
            runningTimer?.stop()
        }
        runningTimer = timer
        timer.start()
    }

    override fun stop(timer: Timer) {
        Log.d("TAG", "MainActivity stop()")
        timer.stop()
    }

    override fun delete(timer: Timer) {
        timers.remove(timer)
        timerAdapter.submitList(timers.toList())
    }

    companion object {
        private const val TIMER_CHECK_PERIOD = 100L
    }
}