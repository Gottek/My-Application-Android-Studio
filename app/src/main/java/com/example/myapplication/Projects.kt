package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Projects(val idProject:String?, var title: String?, var state:Boolean, var description:String?, var mailUser:String?):Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString()
    )
    constructor():this("ke sao","salut",false,"comment","tuvaus@hotmail.com")
    constructor(title: String?,state:Boolean,description:String?, mailUser:String?):this(UUID.randomUUID().toString(),title,state,description,mailUser)


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idProject)
        parcel.writeString(title)
        parcel.writeByte(if (state) 1 else 0)
        parcel.writeString(description)
        parcel.writeString(mailUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Projects

        if (title != other.title) return false
        if (state != other.state) return false
        if (description != other.description) return false
        if (mailUser != other.mailUser) return false

        return true
    }

    override fun hashCode(): Int {
        return idProject?.hashCode() ?: 0
    }


    companion object CREATOR : Parcelable.Creator<Projects> {
        override fun createFromParcel(parcel: Parcel): Projects {
            return Projects(parcel)
        }

        override fun newArray(size: Int): Array<Projects?> {
            return arrayOfNulls(size)
        }

        private fun generateId():String{
           return UUID.randomUUID().toString()
        }
    }

}