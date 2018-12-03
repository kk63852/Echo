package com.example.lenovo.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.lenovo.echo.Activity.MainActivity
import com.example.lenovo.echo.Fragment.AboutUsFragment
import com.example.lenovo.echo.Fragment.FavoriteFragment
import com.example.lenovo.echo.Fragment.MainScreenFragment
import com.example.lenovo.echo.Fragment.SettingsFragment
import com.example.lenovo.echo.R

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImage: IntArray, _context: Context) :
    RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    var contentList: ArrayList<String>? = null
    var getIMage: IntArray? = null
    var mContext: Context? = null

    init {
        this.contentList = _contentList
        this.getIMage = _getImage
        this.mContext = _context
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavViewHolder {
        var itemView = LayoutInflater.from(p0?.context)
            .inflate(R.layout.row_custom_navigationdrawer, p0, false)
        val returnThis = NavViewHolder(itemView)
        return returnThis
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder?.icon_get?.setBackgroundResource(getIMage?.get(position) as Int)
        holder?.text_get?.setText(contentList?.get(position))
        holder?.content_holder?.setOnClickListener({
            if (position == 0) {
                val mainScreenFragment = MainScreenFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, mainScreenFragment)
                    .commit()
            } else if (position == 1) {
                val favoriteFragment = FavoriteFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, favoriteFragment)
                    .commit()
            } else if (position == 2) {
                val settingsFragment = SettingsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, settingsFragment)
                    .commit()
            } else {
                val aboutUsFragment = AboutUsFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment, aboutUsFragment)
                    .commit()
            }
            MainActivity.Statisfied.drawerLayout?.closeDrawers()
        })
    }

    class NavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon_get: ImageView? = null
        var text_get: TextView? = null
        var content_holder: RelativeLayout? = null

        init {
            icon_get = itemView?.findViewById(R.id.icon_navdrawer)
            text_get = itemView?.findViewById(R.id.text_navdrawer)
            content_holder = itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }

    }

}