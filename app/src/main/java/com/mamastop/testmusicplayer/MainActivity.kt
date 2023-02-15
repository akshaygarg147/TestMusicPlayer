package com.mamastop.testmusicplayer

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mamastop.testmusicplayer.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    var myReceiver: MyReceiver? = null
    lateinit var items: Array<String?>
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
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    fun findSong(root: File): ArrayList<File> {
        val at = ArrayList<File>()
        val files = root.listFiles()
        for (singleFile in files) {
            if (singleFile.isDirectory && !singleFile.isHidden) {
             //   at.addAll(findSong(singleFile))
            } else {
                if (singleFile.name.endsWith(".mp3") ||
                    singleFile.name.endsWith(".wav")
                ) {
                    at.add(singleFile)
                }
            }
        }
        return at
    }

    fun display() {
        val mySongs = findSong(Environment.getExternalStorageDirectory())
        items = arrayOfNulls(mySongs.size)
        for (i in mySongs.indices) {
            //toast(mySongs.get(i).getName().toString());
            items[i] = mySongs[i].name.toString().replace(".mp3", "").replace(".wav", "")
        }
        val adp = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        binding?. listView!!.adapter = adp
        binding?. listView!!.onItemClickListener =
            OnItemClickListener { adapterView, view, position, l ->
                val songName = binding?.listView?.get(position).toString()
              //  startService(Intent(this, SampleForegroundService::class.java).setAction(Constants.object1.STARTFOREGROUND_ACTION))
                myReceiver = MyReceiver()
                val intentFilter = IntentFilter()
                intentFilter.addAction(SampleForegroundService.MY_ACTION)
                registerReceiver(myReceiver, intentFilter)
                startService(
                    Intent(this, SampleForegroundService::class.java)
                        .putExtra("pos", position).putExtra("songs", mySongs)
                        .putExtra("songname", songName)
                        .  setAction(Constants.STARTFOREGROUND_ACTION))

            }
    }

    companion object{
        const val  ACTION_STOP = "${BuildConfig.APPLICATION_ID}.stop"
    }
    class MyReceiver() : BroadcastReceiver() {
        override fun onReceive(arg0: Context?, arg1: Intent) {
            // TODO Auto-generated method stub
            val datapassed = arg1.getStringExtra("DATAPASSED")
            if(datapassed=="Next"){
                Toast.makeText(arg0,"next button called",Toast.LENGTH_LONG).show()
//                           mp!!.stop()
//            mp!!.release()
//            position = (position + 1) % mySongs!!.size
//            val u = Uri.parse(mySongs!![position].toString())
//            // songNameText.setText(getSongName);
//           mp = MediaPlayer.create(applicationContext, u)
//            sname = mySongs[position].toString()
////            songNameText!!.text = sname
//            try {
//              mp!!.start()
//            } catch (e: Exception) {
//            }
            }
            else   if(datapassed=="Prev"){
                Toast.makeText(arg0,"previous button called",Toast.LENGTH_LONG).show()
                //           mp!!.stop()
//            mp!!.release()
//            position = (position + 1) % mySongs!!.size
//            val u = Uri.parse(mySongs!![position].toString())
//            // songNameText.setText(getSongName);
//           mp = MediaPlayer.create(applicationContext, u)
//            sname = mySongs[position].toString()
////            songNameText!!.text = sname
//            try {
//              mp!!.start()
//            } catch (e: Exception) {
//            }
            }
            else   if(datapassed==""){
                //           mp!!.stop()
//            mp!!.release()
//            position = (position + 1) % mySongs!!.size
//            val u = Uri.parse(mySongs!![position].toString())
//            // songNameText.setText(getSongName);
//           mp = MediaPlayer.create(applicationContext, u)
//            sname = mySongs[position].toString()
////            songNameText!!.text = sname
//            try {
//              mp!!.start()
//            } catch (e: Exception) {
//            }
            }
            Log.d("datapassed",datapassed.toString())

        }
    }
}
