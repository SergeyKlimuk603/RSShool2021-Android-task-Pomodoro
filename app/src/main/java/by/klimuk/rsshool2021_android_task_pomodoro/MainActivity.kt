package by.klimuk.rsshool2021_android_task_pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import by.klimuk.rsshool2021_android_task_pomodoro.adapters.TimerAdapter
import by.klimuk.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import by.klimuk.rsshool2021_android_task_pomodoro.interfaces.TimerListener
import by.klimuk.rsshool2021_android_task_pomodoro.models.Metronome
import by.klimuk.rsshool2021_android_task_pomodoro.models.Timer

class MainActivity : AppCompatActivity(), Metronome.CallBack,
    TimerListener, Timer.TimerListener {

    private lateinit var binding: ActivityMainBinding

    private val timerAdapter = TimerAdapter(this)
    private val timers = mutableListOf<Timer>()
    private var nextId = 0
    private val metronome = Metronome(this, TIMER_CHECK_PERIOD)
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
            timers.add(Timer(this, nextId++, 60 * 1000))
            timerAdapter.submitList(timers.toList())
        }
    }

    // Переопределенные функции интерфейсов
    override fun tick() {
        //Log.d("TAG", "MainActivity tick()---------------")
        if (runningTimer != null) {
            runningTimer?.updateTime(System.currentTimeMillis())
        }
    }

    override fun finish(timer: Timer) {
        TODO("Закончилось время таймера")
    }

    override fun updateTime(timer: Timer) {
        Log.d("TAG", "MainActivity updateTime()---------------")
        timerAdapter.notifyDataSetChanged()
    }

    override fun start(timer: Timer) {
        Log.d("TAG", "MainActivity start() startTimerId = ${timer.id}")
        if (runningTimer != null) {
            runningTimer?.stop(System.currentTimeMillis())
        }
        runningTimer = timer
        metronome.start()
        timer.start(System.currentTimeMillis())
        updateTime(timer)
    }

    override fun stop(timer: Timer) {
        Log.d("TAG", "MainActivity stop()")
        metronome.stop()
        timer.stop(System.currentTimeMillis())
    }

    override fun delete(timer: Timer) {
        if (timer.isRunning) {
            metronome.stop()
        }
        timers.remove(timer)
        timerAdapter.submitList(timers.toList())
    }

    companion object {
        private const val TIMER_CHECK_PERIOD = 100L
    }
}