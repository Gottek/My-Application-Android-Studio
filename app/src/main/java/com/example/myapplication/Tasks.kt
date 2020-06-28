package com.example.myapplication

import java.util.*

data class Tasks(var idTask:Int, var text : String, var isDone:Boolean , var idSection:Int) {
    constructor() : this(45, "salut", false, 23)
    constructor(theText: String, state: Boolean, idSection: Int)
            : this(UUID.randomUUID().hashCode(), theText, false, idSection)

}