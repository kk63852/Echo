package com.example.lenovo.echo.Fragment


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.lenovo.echo.Activity.MainActivity
import com.example.lenovo.echo.Database.EchoDatabase
import com.example.lenovo.echo.Fragment.FavoriteFragment.Statified.mediaPlayer
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Staticated.onSongComplete
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Staticated.playNext
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Staticated.processInformation
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Staticated.updateTextViews
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.audioVisualisationView
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.currentPosition
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.currentSongHelper
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.endTimeText
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.fab
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.favoriteContent
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.fetchSongs
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.glView
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.loopImageButton
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.mediaPlayer
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.myActivity
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.nextImageButton
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.playPauseImageButton
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.previousImageButton
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.shuffleImageButton
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.songArtistView
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.songTitleView
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.startTimeText
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.updateSongTime
import com.example.lenovo.echo.R
import com.example.lenovo.echo.R.id.seekBar
import com.example.lenovo.echo.Songs
import java.util.*
import java.util.concurrent.TimeUnit


class SongPlayingFragment : Fragment() {
    object Statified {
        var myActivity: Activity? = null

        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualisationView: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var favoriteContent: EchoDatabase? = null
        var MY_PREFS_NAME = "ShakeFeature"
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var updateSongTime = object : Runnable {
            override fun run() {
                val getCurrent = mediaPlayer?.currentPosition
                startTimeText?.setText(
                    String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long) -
                                TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))

                    )
                )
                seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }

        }
    }


    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"
        fun onSongComplete() {

            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            } else {

                if (Statified.currentSongHelper?.isLoop as Boolean) {
                    Statified.currentSongHelper?.isPlaying = true
                    var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.currentPosition = Statified.currentPosition
                    Statified.currentSongHelper?.songPath = nextSong?.songData
                    Statified.currentSongHelper?.songTitle = nextSong?.songTitle
                    Statified.currentSongHelper?.songArtist = nextSong?.songArtist
                    Statified.currentSongHelper?.songId = nextSong?.songId as Long
                    updateTextViews(
                        Statified.currentSongHelper?.songTitle as String,
                        Statified.currentSongHelper?.songArtist as String
                    )

                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(
                            Statified.myActivity,
                            Uri.parse(Statified.currentSongHelper?.songPath)
                        )
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {

                    playNext("PlayNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
                if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                    Statified.fab?.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity as Context,
                            R.drawable.navigation_favorites
                        )
                    )
                } else {
                    Statified.fab?.setImageDrawable(
                        ContextCompat.getDrawable(
                            myActivity as Context,
                            R.drawable.favorite_off
                        )
                    )
                }
            }
        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated = songTitle
            if (songTitle.equals("unknown", true)) {
                songTitleUpdated = "unknown"
            }
            var songArtistUpdated = songArtist
            if (songTitle.equals("unknown", true)) {
                songArtistUpdated = "unknown"
            }
            Statified.songTitleView?.setText(songTitleUpdated)
            Statified.songArtistView?.setText(songArtistUpdated)
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            Statified.seekBar?.max = finalTime
            Statified.startTimeText?.setText(
                String.format(
                    "%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
                )
            )

            Statified.endTimeText?.setText(
                String.format(
                    "%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
                )
            )

            Statified.seekBar?.setProgress(startTime)

            Handler().postDelayed(Statified.updateSongTime, 1000)
            Statified.seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                    Toast.makeText(Statified.myActivity, "Progress is " + seekBar.progress + "%", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }

        fun playNext(check: String) {

            if (check.equals("PlayNextNormal", true)) {
                Statified.currentPosition = Statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition
            }
            if (Statified.currentPosition == Statified.fetchSongs?.size) {
                Statified.currentPosition = 0
            }
            Statified.currentSongHelper?.isLoop = false
            var nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.songPath = nextSong?.songData
            Statified.currentSongHelper?.songTitle = nextSong?.songTitle
            Statified.currentSongHelper?.songArtist = nextSong?.songArtist
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition
            Statified.currentSongHelper?.songId = nextSong?.songId as Long
            updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )
            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(
                    Statified.myActivity as Context
                    , Uri.parse(Statified.currentSongHelper?.songPath)
                )
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity as Context,
                        R.drawable.navigation_favorites
                    )
                )
            } else {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
            }

        }
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)

        setHasOptionsMenu(true)
        activity?.title = "Now Playing"
        Statified.seekBar = view?.findViewById(R.id.seekBar)
        Statified.startTimeText = view?.findViewById(R.id.startTime)
        Statified.endTimeText = view?.findViewById(R.id.endTime)
        Statified.playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        Statified.nextImageButton = view?.findViewById(R.id.nextButton)
        Statified.previousImageButton = view?.findViewById(R.id.previousButton)
        Statified.loopImageButton = view?.findViewById(R.id.loopButton)
        Statified.shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        Statified.songArtistView = view?.findViewById(R.id.songArtist)
        Statified.songTitleView = view?.findViewById(R.id.songTitle)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.fab = view?.findViewById(R.id.favoriteIcon)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualisationView = glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualisationView?.onResume()
        Statified.mSensorManager?.registerListener(
            Statified.mSensorListener,
            Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        Statified.audioVisualisationView?.onPause()
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)

    }

    override fun onDestroy() {
        Statified.audioVisualisationView?.release()
        super.onDestroy()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Statified.mSensorManager =
                Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager


        mAcceleration = 0.0f

        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH

        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.action_redirect -> {
                Statified.myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Statified.favoriteContent = EchoDatabase(Statified.myActivity)
        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLoop = false
        Statified.currentSongHelper?.isShuffle = false
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0
        try {
            _songArtist = arguments!!.getString("songArtist")
            _songTitle = arguments!!.getString("songTitle")
            path = arguments!!.getString("path")
            songId = arguments!!.getInt("songId").toLong()
            Statified.currentPosition = arguments!!.getInt("songPosition")
            Statified.fetchSongs = arguments!!.getParcelableArrayList("songData")
            Statified.currentSongHelper?.songArtist = _songArtist
            Statified.currentSongHelper?.songTitle = _songTitle
            Statified.currentSongHelper?.songPath = path
            Statified.currentSongHelper?.songId = songId
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            updateTextViews(
                Statified.currentSongHelper?.songTitle as String,
                Statified.currentSongHelper?.songArtist as String
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null) {
            Statified.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
        } else {


            Statified.mediaPlayer = MediaPlayer()

            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {

                Statified.mediaPlayer?.setDataSource(myActivity, Uri.parse(path))

                Statified.mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Statified.mediaPlayer?.start()

        }
        processInformation(Statified.mediaPlayer as MediaPlayer)
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.mediaPlayer?.setOnCompletionListener {
            onSongComplete()
        }
        clickHandler()
        var visualisationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        Statified.audioVisualisationView?.linkTo(visualisationHandler)
        var prefsForShuffle = Statified.myActivity?.getSharedPreferences(
            Staticated.MY_PREFS_SHUFFLE,
            Context.MODE_PRIVATE
        )

        var isShuffleAllowed = prefsForShuffle?.getBoolean("feaure", false)
        if (isShuffleAllowed as Boolean) {

            Statified.currentSongHelper?.isShuffle = true
            Statified.currentSongHelper?.isLoop = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            Statified.currentSongHelper?.isShuffle = false
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefsForLoop = Statified.myActivity?.getSharedPreferences(
            Staticated.MY_PREFS_LOOP,
            Context.MODE_PRIVATE
        )

        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {

            Statified.currentSongHelper?.isShuffle = false
            Statified.currentSongHelper?.isLoop = true
            Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {

            Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            Statified.currentSongHelper?.isLoop = false
        }
        if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    myActivity as Context,
                    R.drawable.navigation_favorites
                )
            )
        } else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context, R.drawable.favorite_off))
        }


    }

    fun clickHandler() {

        Statified.fab?.setOnClickListener({
            if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int)
                        as Boolean
            ) {
                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity as Context,
                        R.drawable.favorite_off
                    )
                )
                Statified.favoriteContent?.deleteFavourite(
                    Statified.currentSongHelper?.songId?.toInt() as
                            Int
                )

                Toast.makeText(
                    Statified.myActivity, "Removed from Favorites",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                Statified.fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity as Context,
                        R.drawable.navigation_favorites
                    )
                )
                Statified.favoriteContent?.storeAsFavorite(
                    Statified.currentSongHelper?.songId?.toInt(),
                    Statified.currentSongHelper?.songArtist,
                    Statified.currentSongHelper?.songTitle,
                    Statified.currentSongHelper?.songPath
                )
                Toast.makeText(
                    Statified.myActivity, "Added to Favorites",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


        Statified.shuffleImageButton?.setOnClickListener({
            var editorShuffle =
                Statified.myActivity?.getSharedPreferences(
                    SongPlayingFragment.Staticated.MY_PREFS_SHUFFLE,
                    Context.MODE_PRIVATE
                )
                    ?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(
                SongPlayingFragment.Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                Statified.currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                Statified.currentSongHelper?.isShuffle = true
                Statified.currentSongHelper?.isLoop = false
                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })
        Statified.nextImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }
        })
        Statified.previousImageButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true

            if (Statified.currentSongHelper?.isLoop as Boolean) {

                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }

            playPrevious()
        })
        Statified.loopImageButton?.setOnClickListener({
            var editorShuffle =
                Statified.myActivity?.getSharedPreferences(
                    SongPlayingFragment.Staticated.MY_PREFS_SHUFFLE,
                    Context.MODE_PRIVATE
                )
                    ?.edit()
            var editorLoop = myActivity?.getSharedPreferences(
                Staticated.MY_PREFS_LOOP,
                Context.MODE_PRIVATE
            )?.edit()
            if (Statified.currentSongHelper?.isLoop as Boolean) {

                Statified.currentSongHelper?.isLoop = false

                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {

                Statified.currentSongHelper?.isLoop = true

                Statified.currentSongHelper?.isShuffle = false

                Statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

                Statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }

        })
        Statified.playPauseImageButton?.setOnClickListener({

            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

            } else {
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }

        })
    }


    fun playPrevious() {

        Statified.currentPosition = Statified.currentPosition - 1

        if (Statified.currentPosition == -1) {
            Statified.currentPosition = 0
        }
        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        Statified.currentSongHelper?.isLoop = false

        var nextSong = fetchSongs?.get(currentPosition)
        Statified.currentSongHelper?.songPath = nextSong?.songData
        Statified.currentSongHelper?.songTitle = nextSong?.songTitle
        Statified.currentSongHelper?.songArtist = nextSong?.songArtist
        Statified.currentSongHelper?.songId = nextSong?.songId as Long
        updateTextViews(
            Statified.currentSongHelper?.songTitle as String,
            Statified.currentSongHelper?.songArtist as String
        )
        Statified.mediaPlayer?.reset()
        try {
            Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.songPath))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            processInformation(Statified.mediaPlayer as MediaPlayer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (Statified.favoriteContent?.checkifIdExists(Statified.currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            Statified.fab?.setImageDrawable(
                ContextCompat.getDrawable(
                    myActivity as Context,
                    R.drawable.navigation_favorites
                )
            )
        } else {
            Statified.fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context, R.drawable.favorite_off))
        }

    }

    fun bindShakeListener() {

        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent) {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                mAccelerationLast = mAccelerationCurrent

                mAccelerationCurrent = Math.sqrt(
                    ((x * x + y * y + z *
                            z).toDouble())
                ).toFloat()

                val delta = mAccelerationCurrent - mAccelerationLast
                mAcceleration = mAcceleration * 0.9f + delta

                if (mAcceleration > 12) {

                    val prefs =
                        Statified.myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }
}

