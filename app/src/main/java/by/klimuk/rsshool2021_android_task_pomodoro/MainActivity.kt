package by.klimuk.rsshool2021_android_task_pomodoro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import by.klimuk.rsshool2021_android_task_pomodoro.adapters.TimerAdapter
import by.klimuk.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import by.klimuk.rsshool2021_android_task_pomodoro.interfaces.TimerListener
import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer
import by.klimuk.rsshool2021_android_task_pomodoro.services.ForegroundService

class MainActivity : AppCompatActivity(), TimerListener, Timer.TimerListener,
    LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimerAdapter(this)
    private val timers = mutableListOf<Timer>()
    private var nextId = 0
    private var runningTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

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
                val toast = Toast.makeText(
                    this,
                    "Значение уставки должно быть в пределах от 1 до 5999 минут",
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            binding.etEnterTime.clearFocus()
        }
    }

    // Переопределенные функции интерфейсов
    override fun updateTime(timer: Timer) {
        //Log.d("TAG", "MainActivity updateTime()---------------")
        timerAdapter.notifyDataSetChanged()
    }

    override fun finish(timer: Timer) {
        val toast = Toast.makeText(
            this,
            "Время вышло!!!",
            Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
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
        runningTimer = null
    }

    override fun delete(timer: Timer) {
        timers.remove(timer)
        timerAdapter.submitList(timers.toList())
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (runningTimer != null) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(TIME_LEFT_MS, runningTimer?.timeLeft)
            startIntent.putExtra(START_SERVICE_TIME, System.currentTimeMillis())
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        runningTimer?.stop()
        runningTimer = null
    }

    companion object {
        private const val TIMER_CHECK_PERIOD = 100L
    }
}