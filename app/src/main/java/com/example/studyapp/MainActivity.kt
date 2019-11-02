package com.example.studyapp

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NotificationCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler:Handler = Handler()
    private var pause:Boolean = false

    private lateinit var mNotificationHelper: NotificationHelper



    override fun onBackPressed() {
//        super.onBackPressed()
        Toast.makeText(this, "Back pressed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mNotificationHelper = NotificationHelper(this)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        var mp3list = listOf(R.raw.m1, R.raw.m2, R.raw.m3, R.raw.m4, R.raw.m5, R.raw.m6, R.raw.m7, R.raw.m8, R.raw.m9, R.raw.m10, R.raw.m11, R.raw.m12, R.raw.m13)
        val rnds = (0..12).random()
        // Start the media player
        playBtn.setOnClickListener{
            if(pause) {
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            }
            else {
                mediaPlayer = MediaPlayer.create(applicationContext, mp3list[rnds])
                mediaPlayer.start()
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            }
            initializeSeekBar()
            playBtn.isEnabled = false
            pauseBtn.isEnabled = true
            stopBtn.isEnabled = true

            mediaPlayer.setOnCompletionListener {
//                playBtn.isEnabled = true
//                pauseBtn.isEnabled = false
//                stopBtn.isEnabled = false
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
                Toast.makeText(this, "end, go to start", Toast.LENGTH_SHORT).show()
            }
        }

        // Pause the media player
        pauseBtn.setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.pause()
                pause = true
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = true
                Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
            }
        }

        // Stop the media player
        stopBtn.setOnClickListener{
            if(mediaPlayer.isPlaying || pause.equals(true)){
                pause = false
                seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                tv_pass.text = ""
                tv_due.text = ""
                Toast.makeText(this, "media stop", Toast.LENGTH_SHORT).show()
            }
        }


        buttonChannel1.setOnClickListener {
            sendOnChannel1(edittext_title.text.toString(), edittext_message.text.toString())
        }

        buttonChannel2.setOnClickListener {
            sendOnChannel2(edittext_title.text.toString(), edittext_message.text.toString())
        }



    }

    fun sendOnChannel1(title: String, message: String) {
        val nb: NotificationCompat.Builder = mNotificationHelper.getChannel1Notification(title, message)
        mNotificationHelper.getManager().notify(1, nb.build())
    }

    fun sendOnChannel2(title: String, message: String) {
        val nb: NotificationCompat.Builder = mNotificationHelper.getChannel1Notification(title, message)
        mNotificationHelper.getManager().notify(2, nb.build())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Method to initialize seek bar and audio states
    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
}

// Creating an extension property to get the media player time
val MediaPlayer.seconds:Int
    get() {
        return this.duration / 1000
    }

// Creating an extension property to get media player current position in seconds
val MediaPlayer.currentSeconds:Int
    get() {
        return this.currentPosition / 1000
    }