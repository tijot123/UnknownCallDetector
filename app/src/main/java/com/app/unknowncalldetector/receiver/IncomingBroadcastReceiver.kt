package com.app.unknowncalldetector.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract.PhoneLookup
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.app.unknowncalldetector.listener.CustomPhoneStateListener
import com.app.unknowncalldetector.listener.IncomingPhoneStateListener

class IncomingBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.PHONE_STATE") {
            val telephonyManager: TelephonyManager =
                context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val tm = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val phoneStateListener = IncomingPhoneStateListener(object : CustomPhoneStateListener {
                @SuppressLint("MissingPermission")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    when (state) {
                        TelephonyManager.CALL_STATE_IDLE -> {

                        }
                        TelephonyManager.CALL_STATE_OFFHOOK -> {
                            Log.e("CALL_STATE_OFFHOOK", "onCallStateChanged: ")
                        }
                        TelephonyManager.CALL_STATE_RINGING -> {
                            Log.e("IncomingPhoneStateListener", "onCallStateChanged: $phoneNumber")
                            try {
                                val resolver: ContentResolver = context.contentResolver
                                val uri: Uri = Uri.withAppendedPath(
                                    PhoneLookup.CONTENT_FILTER_URI,
                                    Uri.encode(phoneNumber)
                                )
                                val c: Cursor? = resolver.query(
                                    uri,
                                    arrayOf(PhoneLookup.DISPLAY_NAME),
                                    null,
                                    null,
                                    null
                                )
                                if (c != null && c.moveToFirst()) { // cursor not null means number is found contactsTable
                                    tm.acceptRingingCall()
                                    openCallRecordActivity(context)
                                    c.close()
                                } else {
                                    tm.acceptRingingCall()
                                    openCallRecordActivity(context)
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                    }
                }
            })
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    fun openCallRecordActivity(context: Context) {
        val i = Intent()
        i.setClassName("com.app.unknowncalldetector", "com.app.unknowncalldetector.MainActivity")
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra("isMessageNeeded", true)
        context.startActivity(i)
    }
}