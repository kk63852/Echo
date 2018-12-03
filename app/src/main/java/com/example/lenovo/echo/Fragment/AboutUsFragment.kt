package com.example.lenovo.echo.Fragment


import android.app.Activity
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.lenovo.echo.Fragment.SongPlayingFragment.Statified.myActivity
import com.example.lenovo.echo.R
import kotlinx.android.synthetic.main.fragment_about_us.*


class AboutUsFragment : Fragment() {
    var songTitle: TextView? = null
    var trackPosition: Int = 0
    var playPauseButton: ImageButton? = null
    var nowPlayingBottomBar: RelativeLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_about_us, container, false)
        activity?.title = "About Us"
        songTitle = view.findViewById(R.id.songTitleFavScreen)
        nowPlayingBottomBar = view.findViewById(R.id.hiddenBarFavScreen)
        playPauseButton = view.findViewById(R.id.playPauseButton)

        setHasOptionsMenu(true)
        return view
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




