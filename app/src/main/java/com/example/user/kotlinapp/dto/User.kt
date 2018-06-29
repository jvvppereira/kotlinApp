package com.example.user.kotlinapp.dto

import java.io.Serializable

class User : Serializable {
    var uid: String = ""

    constructor() //Default constructor required for calls to DataSnapshot.getValue()

    constructor(uid: String) {
        this.uid = uid
    }
}