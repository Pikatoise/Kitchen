package com.example.kitchen.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.IOException
import java.sql.SQLException

class PreferencesRepository(context: Context){
    private var mDbHelper: DatabaseHelper
    private var mDb: SQLiteDatabase

    init{
        mDbHelper = DatabaseHelper(context)

        try {
            mDbHelper.updateDataBase()
        } catch (mIOException: IOException) {
            throw Error("UnableToUpdateDatabase")
        }

        mDb = try {
            mDbHelper.writableDatabase
        } catch (mSQLException: SQLException) {
            throw mSQLException
        }
    }

    public fun getProfileId(): Int {
        var profileId: Int = -1

        var cursor = mDb.rawQuery(
            "SELECT profileId FROM preferences",
            null
        )

        if (cursor != null && cursor.count > 0){
            cursor.moveToFirst()

            profileId = cursor.getInt(0)
        }

        cursor.close()

        return profileId
    }

    public fun updateProfileId(id: Int) {
        mDb.execSQL("UPDATE preferences SET profileId = ${id}")
    }
}