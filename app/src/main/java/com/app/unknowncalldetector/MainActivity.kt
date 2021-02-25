package com.app.unknowncalldetector

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.app.unknowncalldetector.databinding.ActivityMainBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@RuntimePermissions
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var recorder: AudioRecord? = null
    private val SAMPLING_RATE_IN_HZ = 44100
    private val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT
    /**
     * Factor by that the minimum buffer size is multiplied. The bigger the factor is the less
     * likely it is that samples will be dropped, but more memory will be used. The minimum buffer
     * size is determined by [AudioRecord.getMinBufferSize] and depends on the
     * recording settings.
     */
    private val BUFFER_SIZE_FACTOR = 2
    /**
     * Size of the buffer where the audio data is stored by Android
     */
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(
        SAMPLING_RATE_IN_HZ,
        CHANNEL_CONFIG, AUDIO_FORMAT
    ) * BUFFER_SIZE_FACTOR
    /**
     * Signals whether a recording is in progress (true) or not (false).
     */
    private val recordingInProgress: AtomicBoolean = AtomicBoolean(false)
    private var recordingThread: Thread? = null
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

        if (isMessageNeeded) {
            recorder?.startRecording()
            recordingInProgress.set(true)
            recorder?.let { rec ->
                recordingThread = Thread(
                    RecordingRunnable(BUFFER_SIZE, recordingInProgress, rec),
                    "Recording Thread"
                )
                recordingThread?.start()
            }
        }
    }

    private fun stopRecording() {
        if (null == recorder) {
            return
        }
        recordingInProgress.set(false)
        recorder?.stop()
        recorder?.release()
        recorder = null
        recordingThread = null
    }

    /** @since 24-02-2021
     * Manifest.permission.ANSWER_PHONE_CALLS needs API Level 26
     * @author Tijo Thomas
     * */
    @NeedsPermission(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    fun queryContactsAndDetectPhoneCalls() {
        binding.message.text =
            getString(R.string.message)
        recorder = AudioRecord(
            MediaRecorder.AudioSource.DEFAULT, SAMPLING_RATE_IN_HZ,
            CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE
        )
    }

    override fun onPause() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        stopRecording()
        super.onPause()
    }
}