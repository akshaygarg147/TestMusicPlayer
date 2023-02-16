package com.mamastop.testmusicplayer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi


class SampleForegroundService : Service() {

    //set up service
    //sending broadcast to main activity
    val LOG_TAG = "clickedoperation"

    companion object {
        val MY_ACTION = "MY_ACTION"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action.equals(Constants.STARTFOREGROUND_ACTION)) {
            var songName = ""
            val i = intent
            val stringExtra = i?.getStringExtra("clickoperation")
            if (i?.extras?.containsKey("songname") == true) {
                songName =
                    (i.extras?.getString("songname")?.split("**")?.toTypedArray()?.get(0) ?: "")
            }
            if (stringExtra.equals("Pause")) {
                shownotification(songName, R.drawable.play)
            } else {
                shownotification(songName)
            }


        } else if (intent?.action.equals(Constants.PREV_ACTION)) {
            val intent = Intent()
            intent.action = MY_ACTION
            intent.putExtra("DATAPASSED", "Prev")
            sendBroadcast(intent)
        } else if (intent?.action.equals(Constants.PLAY_ACTION)) {
            val intentPause = Intent()
            intentPause.action = MY_ACTION
            intentPause.putExtra("DATAPASSED", "Pause")
            sendBroadcast(intentPause)


        } else if (intent?.action.equals(Constants.PAUSE_ACTION)) {
            val intentPause = Intent()
            intentPause.action = MY_ACTION
            intentPause.putExtra("DATAPASSED", "Pause")
            sendBroadcast(intentPause)


        } else if (intent?.action.equals(Constants.NEXT_ACTION)) {
            val intentnext = Intent()
            intentnext.action = MY_ACTION
            intentnext.putExtra("DATAPASSED", "Next")
            sendBroadcast(intentnext)

        } else if (intent?.action.equals(
                Constants.STOPFOREGROUND_ACTION
            )
        ) {
            stopSelf()
        }

        return START_STICKY

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shownotification(
        sonName: String, drawable: Int = R.drawable.pause
    ) {
        val bigView = RemoteViews(
            applicationContext.packageName,
            R.layout.activity_player
        )
        bigView.setImageViewResource(R.id.pause, drawable)
        bigView.setTextViewText(R.id.tvSongName, sonName)
        val previousIntent = Intent(this, SampleForegroundService::class.java)
        previousIntent.action = Constants.PREV_ACTION
        val ppreviousIntent = PendingIntent.getService(
            this, 0,
            previousIntent, PendingIntent.FLAG_MUTABLE
        )
        val playIntent = Intent(this, SampleForegroundService::class.java)
        playIntent.action = Constants.object1.PLAY_ACTION
        val pplayIntent = PendingIntent.getService(
            this, 0,
            playIntent, PendingIntent.FLAG_MUTABLE
        )
        val nextIntent = Intent(this, SampleForegroundService::class.java)
        nextIntent.action = Constants.object1.NEXT_ACTION
        val pnextIntent = PendingIntent.getService(
            this, 0,
            nextIntent, PendingIntent.FLAG_MUTABLE
        )
        bigView.setOnClickPendingIntent(R.id.next, pnextIntent)
        bigView.setOnClickPendingIntent(R.id.pause, pplayIntent)
        bigView.setOnClickPendingIntent(R.id.previous, ppreviousIntent)
        val channelid = "Song"
        val notificationBuilder = Notification.Builder(this, channelid)
            .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
            .setCustomBigContentView(bigView)
            .build()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelid,
                "Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder)
    }


}