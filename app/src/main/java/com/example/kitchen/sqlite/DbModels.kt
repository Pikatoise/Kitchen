package com.example.kitchen.sqlite

import android.os.Parcel
import android.os.Parcelable

data class Preferences(var profileId: Int):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(profileId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Preferences> {
        override fun createFromParcel(parcel: Parcel): Preferences {
            return Preferences(parcel)
        }

        override fun newArray(size: Int): Array<Preferences?> {
            return arrayOfNulls(size)
        }
    }
}