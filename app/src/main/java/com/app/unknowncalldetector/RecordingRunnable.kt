package com.app.unknowncalldetector

import android.media.AudioRecord
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

class RecordingRunnable(
    private var BUFFER_SIZE: Int, private var recordingInProgress: AtomicBoolean,
    private var recorder: AudioRecord
) : Runnable {
    override fun run() {
        val file = File(Environment.getDownloadCacheDirectory(), "recording.pcm")
        val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
        try {
            FileOutputStream(file).use { outStream ->
                while (recordingInProgress.get()) {
                    val result = recorder.read(buffer, BUFFER_SIZE)
                    if (result < 0) {
                        throw RuntimeException(
                            "Reading of audio buffer failed: " +
                                    getBufferReadFailureReason(result)
                        )
                    }
                    outStream.write(buffer.array(), 0, BUFFER_SIZE)
                    buffer.clear()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Writing of recorded audio failed", e)
        }
    }

    private fun getBufferReadFailureReason(errorCode: Int): String {
        return when (errorCode) {
            AudioRecord.ERROR_INVALID_OPERATION -> "ERROR_INVALID_OPERATION"
            AudioRecord.ERROR_BAD_VALUE -> "ERROR_BAD_VALUE"
            AudioRecord.ERROR_DEAD_OBJECT -> "ERROR_DEAD_OBJECT"
            AudioRecord.ERROR -> "ERROR"
            else -> "Unknown ($errorCode)"
        }
    }
}