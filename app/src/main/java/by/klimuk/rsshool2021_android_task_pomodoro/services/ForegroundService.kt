package by.klimuk.rsshool2021_android_task_pomodoro.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import by.klimuk.rsshool2021_android_task_pomodoro.*
import kotlinx.coroutines.*

class ForegroundService : Service() {

    private var isServiceStarted = false
    private var startServiceTime = -1L
    private var notificationManager: NotificationManager? = null
    private var job: Job? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Simple timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_av_timer_24)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        startServiceTime = intent?.extras?.getLong(START_SERVICE_TIME) ?: return
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                val timeLeft = intent?.extras?.getLong(TIME_LEFT_MS) ?: return
                commandStart(timeLeft)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(timeLeft: Long) {
        if (isServiceStarted) {
            return
        }
        //Log.d("TAG", "Service commandStart()")
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(timeLeft)
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer(timeLeft: Long) {
        //Log.d("TAG", "continueTimer()")
        var currentTime = timeLeft
        Log.d("TAG", "currentTime = $currentTime")
        job = GlobalScope.launch(Dispatchers.Main) {
            while(true) {
                currentTime = (timeLeft - (System.currentTimeMillis() - startServiceTime)) / 1000
                if (currentTime < 0) currentTime = 0L
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(
                        (currentTime).displayTime()
                    )
                )
                if (currentTime == 0L) {
                    job?.cancel()
                    break
                }
                delay(INTERVAL)
            }
        }
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        //Log.d("TAG", "Service commandStop()")
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.d("TAG", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            //Log.d("TAG", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        //Log.d("TAG", "startForegroundAndShowNotification()")
        createChannel()
        val notification = getNotification("content")
        //Log.d("TAG", "notification gotten 117")
        startForeground(NOTIFICATION_ID, notification)
        //Log.d("TAG", "started")
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()

    private fun createChannel() {
        //Log.d("TAG", "createChannel()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        //Log.d("TAG", "channel created")
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private companion object {
        private const val CHANNEL_ID = "Channel_ID"
        private const val NOTIFICATION_ID = 777
        private const val INTERVAL = 1000L
    }
}