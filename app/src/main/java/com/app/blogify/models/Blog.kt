package com.app.blogify.models

import android.os.Parcel
import android.os.Parcelable

data class Blog(
    val title: String? = "",
    val username: String? = "",
    val date: String? = "",
    val blog: String? = "",
    var likes: Int = 0,
    val profile: String? = "",
    var isSaved: Boolean = false,
    var blogId: String? = "",
    val likedBy: Map<String, Boolean>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(username)
        parcel.writeString(date)
        parcel.writeString(blog)
        parcel.writeInt(likes)
        parcel.writeString(profile)
        parcel.writeString(blogId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Blog> {
        override fun createFromParcel(parcel: Parcel): Blog {
            return Blog(parcel)
        }

        override fun newArray(size: Int): Array<Blog?> {
            return arrayOfNulls(size)
        }
    }
}
