package com.app.unknowncalldetector

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.unknowncalldetector.databinding.ActivityMainBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        queryContactsAndDetectPhoneCallsWithPermissionCheck()
    }

    /** @since Manifest.permission.ANSWER_PHONE_CALLS needs API Level 26
     * @author Tijo Thomas
     * */
    @NeedsPermission(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG
    )
    fun queryContactsAndDetectPhoneCalls() {
        binding.message.text =
            getString(R.string.message)
    }
}