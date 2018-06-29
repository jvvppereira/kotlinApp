package com.example.user.kotlinapp.dto

import java.io.Serializable
import java.util.*

class Launch : Serializable {
    var paymentDate: Long = 0L
    var dueDate: Long = 0L
    var value: Float = 0F
    var receivement = false
    var name = ""
    var index = 0
    var userUid = ""

    constructor() //Default constructor required for calls to DataSnapshot.getValue()

    fun getPaymentDateAsDate(): Date {
        return Date(paymentDate);
    }

    fun getDueDateAsDate(): Date {
        return Date(dueDate)
    }

    fun Float.format(digits: Int = 2) = java.lang.String.format("%.${digits}f", this)
}