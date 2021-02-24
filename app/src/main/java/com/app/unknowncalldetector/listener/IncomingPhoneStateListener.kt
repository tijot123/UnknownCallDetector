package com.app.unknowncalldetector.listener

import android.telephony.PhoneStateListener

class IncomingPhoneStateListener(private val listener: CustomPhoneStateListener) :
    PhoneStateListener() {
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        listener.onCallStateChanged(state, phoneNumber)
    }
}