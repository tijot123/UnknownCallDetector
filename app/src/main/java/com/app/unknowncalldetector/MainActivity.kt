package com.app.unknowncalldetector

import android.Manifest
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.app.unknowncalldetector.databinding.ActivityMainBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var textToSpeech: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        queryContactsAndDetectPhoneCallsWithPermissionCheck()
        val isMessageNeeded = intent.getBooleanExtra("isMessageNeeded", false)
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech?.language = Locale.UK
                if (isMessageNeeded)
                    textToSpeech?.speak(
                        "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        Random().nextLong().toString()
                    )
            }
        }
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

    override fun onPause() {
        //textToSpeech?.stop()
        //textToSpeech?.shutdown()
        super.onPause()
    }

}