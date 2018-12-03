package com.example.lenovo.echo.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lenovo.echo.Database.EchoDatabase.Staticated.COLUMN_SONG_ARTIST
import com.example.lenovo.echo.Database.EchoDatabase.Staticated.COLUMN_SONG_PATH
import com.example.lenovo.echo.Database.EchoDatabase.Staticated.COLUMN_SONG_TITLE
import com.example.lenovo.echo.Database.EchoDatabase.Staticated.DB_NAME
import com.example.lenovo.echo.Database.EchoDatabase.Staticated.DB_VERSION
import com.example.lenovo.echo.Database.EchoDatabase.Staticated.TABLE_NAME
import com.example.lenovo.echo.Songs
import java.lang.Exception

class EchoDatabase : SQLiteOpenHelper {
    var _songsList = ArrayList<Songs>()


    object Staticated {
        val TABLE_NAME = "FavoriteTable"

        val COLUMN_ID = "SongID"

        val COLUMN_SONG_TITLE = "SongTitle"

        val COLUMN_SONG_ARTIST = "SongArtist"

        val COLUMN_SONG_PATH = "SongPath"
        var DB_VERSION = 1
        val DB_NAME = "FavoriteDatabase"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(
            "CREATE TABLE " + Staticated.TABLE_NAME + "( " + Staticated.COLUMN_ID +
                    " INTEGER," + Staticated.COLUMN_SONG_ARTIST + " STRING," + Staticated.COLUMN_SONG_TITLE + " STRING,"
                    + Staticated.COLUMN_SONG_PATH + " STRING);"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    constructor(context: Context?) : super(
        context,
        Staticated.DB_NAME,
        null,
        Staticated.DB_VERSION
    )

    fun storeAsFavorite(id: Int?, artist: String?, songTitle: String?, path: String?) {

        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(Staticated.COLUMN_ID, id)
        contentValues.put(Staticated.COLUMN_SONG_ARTIST, artist)
        contentValues.put(Staticated.COLUMN_SONG_TITLE, songTitle)
        contentValues.put(Staticated.COLUMN_SONG_PATH, path)


        db.insert(Staticated.TABLE_NAME, null, contentValues)

        db.close()
    }

    fun queryDBList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            val query_params = "SELECT * FROM" + Staticated.TABLE_NAME
            var cSOR = db.rawQuery(query_params, null)
            if (cSOR.moveToFirst()) {
                do {
                    var _id = cSOR.getInt(cSOR.getColumnIndexOrThrow(Staticated.COLUMN_ID))
                    var _artist = cSOR.getString(cSOR.getColumnIndexOrThrow(Staticated.COLUMN_SONG_ARTIST))
                    var _title = cSOR.getString(cSOR.getColumnIndexOrThrow(Staticated.COLUMN_SONG_TITLE))
                    var _path = cSOR.getString(cSOR.getColumnIndexOrThrow(Staticated.COLUMN_SONG_PATH))
                    _songsList.add(Songs(_id as Long, _title, _artist, _path, 0))
                } while (cSOR.moveToNext())
            } else {
                return null
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return _songsList
    }

    fun checkifIdExists(_id: Int): Boolean {

        var storeId = -1090
        val db = this.readableDatabase

        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {

                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(Staticated.COLUMN_ID))
            } while (cSor.moveToNext())
        } else {
            return false
        }
        return storeId != -1090
    }

    fun deleteFavourite(_id: Int) {
        val db = this.writableDatabase

        db.delete(Staticated.TABLE_NAME, Staticated.COLUMN_ID + " = " + _id, null)

        db.close()
    }

    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase

        val query_params = "SELECT * FROM " + Staticated.TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {

                counter = counter + 1
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }

}