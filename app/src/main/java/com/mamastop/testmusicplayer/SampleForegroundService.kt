package com.mamastop.testmusicplayer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi


class SampleForegroundService : Service() {

    //set up service
    //sending broadcast to main activity
    val LOG_TAG = "clickedoperation"
    var mp: MediaPlayer? = null
    companion object {
        val MY_ACTION = "MY_ACTION"
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var mySongs: ArrayList<Parcelable>? = null
        var sname: String? = null
        var position = 0
        if (intent!!.action.equals(Constants.object1.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ")
            val i = intent
            val b = i.extras
            position = b!!.getInt("pos", 0)
            mySongs = b.getParcelableArrayList("songs")
            sname = mySongs!![position].toString()
            val stringExtra = i.getStringExtra("songname")
            position = b.getInt("pos", 0)
            val u = Uri.parse(mySongs[position].toString())
            mp = MediaPlayer.create(applicationContext, u)
            mp!!.start()
            val previousIntent = Intent(this, SampleForegroundService::class.java)
            previousIntent.action = Constants.PREV_ACTION
            val ppreviousIntent = PendingIntent.getService(
                this, 0,
                previousIntent, 0
            )
            val playIntent = Intent(this, SampleForegroundService::class.java)
            playIntent.action = Constants.object1.PLAY_ACTION
            val pplayIntent = PendingIntent.getService(
                this, 0,
                playIntent, 0
            )
            val nextIntent = Intent(this, SampleForegroundService::class.java)
            nextIntent.action = Constants.object1.NEXT_ACTION
            val pnextIntent = PendingIntent.getService(
                this, 0,
                nextIntent, 0
            )


//        contentView . setTextViewText (R.id.tvSongName, songName)

            val channelid = "Song"
            val notificationBuilder = Notification.Builder(this, channelid)
                .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(
                    android.R.drawable.ic_media_previous,
                    "Previous", ppreviousIntent
                )
                .addAction(
                    android.R.drawable.ic_media_play, "Pause",
                    pplayIntent
                )
                .addAction(
                    android.R.drawable.ic_media_next, "Next",
                    pnextIntent
                ).build()
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

//                startForeground(101,
//                    notificationBuilder);

        } else if (intent.action.equals(Constants.PREV_ACTION)) {
            val intent = Intent()
            intent.action = MY_ACTION
            intent.putExtra("DATAPASSED", "Prev")
            sendBroadcast(intent)
        } else if (intent.action.equals(Constants.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play")
            if (mp!!.isPlaying) {
                // pause!!.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp)
                mp!!.pause()
            } else {
//                pause!!.setBackgroundResource(R.drawable.pause)
                mp!!.start()
            }


        } else if (intent.action.equals(Constants.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next")
            val intent = Intent()
            intent.action = MY_ACTION
            intent.putExtra("DATAPASSED", "Next")
            sendBroadcast(intent)

        } else if (intent.action.equals(
                Constants.STOPFOREGROUND_ACTION
            )
        ) {
            stopSelf()
        }

        return START_STICKY

    }


}