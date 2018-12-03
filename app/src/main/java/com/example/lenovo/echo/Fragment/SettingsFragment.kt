package com.example.lenovo.echo.Fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.example.lenovo.echo.R
import com.example.lenovo.echo.R.id.playPauseButton
import com.example.lenovo.echo.R.id.songTitle


class SettingsFragment : Fragment() {
    var myActivity: Activity? = null
    var songTitle: TextView? = null
    var trackPosition: Int = 0
    var playPauseButton: ImageButton? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var shakeSwitch: Switch? = null

    object Statified {
        var MY_PREFS_NAME = "ShakeFeature"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)
        activity?.title = "Settings"
        songTitle = view.findViewById(R.id.songTitleFavScreen)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        shakeSwitch = view?.findViewById(R.id.switchShake)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val prefs = myActivity?.getSharedPreferences(
            Statified.MY_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        var isAllowed = prefs?.getBoolean("feature", false)
        if (isAllowed as Boolean) {

            shakeSwitch?.isChecked = true
        } else {

            shakeSwitch?.isChecked = false
        }

        shakeSwitch?.setOnCheckedChangeListener({ compoundButton, b ->
            if (b) {

                val editor = myActivity?.getSharedPreferences(
                    Statified.MY_PREFS_NAME,
                    Context.MODE_PRIVATE
                )?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {

                val editor = myActivity?.getSharedPreferences(
                    Statified.MY_PREFS_NAME,
                    Context.MODE_PRIVATE
                )?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()
            }
        })
        bottomBarSetup()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({

                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            })
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({

            FavoriteFragment.Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()
            args.putString(
                "songArtist",
                SongPlayingFragment.Statified.currentSongHelper?.songArtist
            )
            args.putString(
                "songTitle",
                SongPlayingFragment.Statified.currentSongHelper?.songTitle
            )
            args.putString(
                "path",
                SongPlayingFragment.Statified.currentSongHelper?.songPath
            )
            args.putInt(
                "songId",
                SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int
            )
            args.putInt(
                "songPosition",
                SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int
            )
            args.putParcelableArrayList(
                "songData",
                SongPlayingFragment.Statified.fetchSongs
            )
            args.putString("FavBottomBar", "success")

            songPlayingFragment.arguments = args
            fragmentManager!!.beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("SongPlayingFragment")
                .commit()
        })
        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition
                        as Int
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }
}

