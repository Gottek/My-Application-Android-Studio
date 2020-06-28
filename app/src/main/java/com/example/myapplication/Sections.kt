package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Sections(
    val idSection: Int,
    var title: String?,
    var state: Boolean,
    var description: String?,
    var idProjects: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString()
    )

    constructor() : this(45, "salut", false, "salut", "salut")
    constructor(title: String, state: Boolean, description: String, idProjects: String)
            : this(UUID.randomUUID().hashCode(), title, false, description, idProjects)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(idSection)
        parcel.writeString(title)
        parcel.writeByte(if (state) 1 else 0)
        parcel.writeString(description)
        parcel.writeString(idProjects)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sections> {
        override fun createFromParcel(parcel: Parcel): Sections {
            return Sections(parcel)
        }

        override fun newArray(size: Int): Array<Sections?> {
            return arrayOfNulls(size)
        }
    }
}