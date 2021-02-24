package com.app.unknowncalldetector.listener

interface CustomPhoneStateListener {
    fun onCallStateChanged(state: Int, phoneNumber: String?)
}