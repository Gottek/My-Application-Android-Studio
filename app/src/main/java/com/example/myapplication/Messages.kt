package com.example.myapplication
import java.util.*

class Messages(var idMessage:Int, var theText: String, var time :String, var mailUser:String) {
    constructor() : this(45, "salut", "jkrger", "dklgjer")
    constructor(theText: String,time:String, mailUser: String)
            : this(UUID.randomUUID().hashCode(), theText, time,mailUser)
}