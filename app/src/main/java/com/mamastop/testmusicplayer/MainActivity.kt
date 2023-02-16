package com.mamastop.testmusicplayer

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mamastop.testmusicplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    companion object Const {
        var mp: MediaPlayer? = null
        var position: Int? = 0
        var mySongs = emptyList<String>()
    }

    var myReceiver: MyReceiver? = null

    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    display()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    fun fetchSongsList(): ArrayList<String> {
        val songList = ArrayList<String>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
        )
        val cursor = (this.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        ))
        while (cursor?.moveToNext() == true) {
            songList.add((cursor.getString(4) ?: "") + "**" + (cursor.getString(3) ?: ""))
        }
        return songList
    }

    fun display() {
        mySongs = fetchSongsList()
        val newList = mySongs.map {
            it.split("**").toTypedArray().get(0)
        }
        val adp = ArrayAdapter(this, android.R.layout.simple_list_item_1, newList)
        binding?.listView?.adapter = adp
        binding?.listView?.onItemClickListener =
            OnItemClickListener { adapterView, view, position1, l ->
                val songName = mySongs[position1]
                myReceiver = MyReceiver()
                val intentFilter = IntentFilter()
                intentFilter.addAction(SampleForegroundService.MY_ACTION)
                registerReceiver(myReceiver, intentFilter)
                position = position1
                if (mp?.isPlaying == true) {
                    mp?.stop()
                    mp?.release()
                }
                val u = Uri.parse(songName.split("**").toTypedArray()[1])
                mp = MediaPlayer.create(applicationContext, u)
                mp?.start()
                startService(
                    Intent(this, SampleForegroundService::class.java)
                        .addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                        .putExtra("songname", songName)
                        .putExtra("clickoperation", "Play")
                        .setAction(Constants.STARTFOREGROUND_ACTION)
                )

            }
    }


    class MyReceiver() : BroadcastReceiver() {
        override fun onReceive(arg0: Context?, arg1: Intent) {
            // TODO Auto-generated method stub
            val datapassed = arg1.getStringExtra("DATAPASSED")
            if (datapassed == "Next") {
                mp?.stop()
                mp?.release()
                position = (position!! + 1) % mySongs.size
                val u = Uri.parse(mySongs[position ?: 0].split("**").toTypedArray()[1])

                mp = MediaPlayer.create(arg0, u)
                try {
                    mp?.start()
                    arg0?.let {
                        it.startService(
                            Intent(it, SampleForegroundService::class.java)
                                .addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                )
                                .putExtra(
                                    "songname",
                                    mySongs[position ?: 0].split("**").toTypedArray()[0]
                                )
                                .putExtra("clickoperation", "Play")
                                .setAction(Constants.STARTFOREGROUND_ACTION)
                        )
                    }
                } catch (e: Exception) {
                }
            } else if (datapassed == "Prev") {
                Toast.makeText(arg0, "previous button called", Toast.LENGTH_LONG).show()
                mp!!.stop()
                mp?.release()
                position = (position!! + 1) % mySongs.size
                val u = Uri.parse(mySongs[position ?: 0].split("**").toTypedArray()[1])
                mp = MediaPlayer.create(arg0, u)

                try {
                    mp?.start()
                } catch (e: Exception) {
                }
                arg0?.let {
                    it.startService(
                        Intent(it, SampleForegroundService::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                            .putExtra(
                                "songname",
                                mySongs[position ?: 0].split("**").toTypedArray()[0]
                            )
                            .putExtra("clickoperation", "Play")
                            .setAction(Constants.STARTFOREGROUND_ACTION)
                    )
                }
            } else if (datapassed == "Pause") {
                Toast.makeText(arg0, "pause button called", Toast.LENGTH_LONG).show()
//                mp?.stop()
                if (mp?.isPlaying == true) {
                    mp?.pause()
                    arg0?.startService(
                        Intent(arg0, SampleForegroundService::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                            ).putExtra(
                                "songname",
                                mySongs[position ?: 0].split("**").toTypedArray()[0]
                            )

                            .putExtra("clickoperation", "Pause")
                            .setAction(Constants.STARTFOREGROUND_ACTION)
                    )
                } else {
                    arg0?.startService(
                        Intent(arg0, SampleForegroundService::class.java)
                            .addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                            .putExtra(
                                "songname",
                                mySongs[position ?: 0].split("**").toTypedArray()[0]
                            )
                            .putExtra("clickoperation", "Play")
                            .setAction(Constants.STARTFOREGROUND_ACTION)
                    )
                    mp?.start()
                }
            }

        }
    }
}
