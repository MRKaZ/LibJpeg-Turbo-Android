/*
 * Copyright 2023 Kasun Gamage
 *
 * [LICENSE]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2023 Kasun Gamage All rights reserved.
 */

@file:Suppress("SameParameterValue")

package com.mrkazofficial.libjpegturbo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.libjpegturbo.turbojpeg.TJ
import org.libjpegturbo.turbojpeg.TJCompressor
import java.io.File
import java.io.FileOutputStream

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Compress example
        val job = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            .async {
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.iso_esfr_enhanced
                ).let { bitmap ->
                    compress(bitmap, 100)
                        .saveImage()
                }
            }
        job.invokeOnCompletion {
            if (it == null) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "Done ${job.getCompleted().absolutePath}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Compress [compress]
     * - Compress example for libjpeg-turbo
     * @param bitmap [Bitmap] source of your image
     * @param quality [Int] image quality (1-100) that need to compress
     * @return [Bitmap]
     */
    private suspend fun compress(bitmap: Bitmap, quality: Int): Bitmap =
        withContext(Dispatchers.IO) {
            val width = bitmap.width
            val height = bitmap.height

            // Limit max compress size for prevent Caused by: java.lang.IllegalArgumentException: Destination buffer is not large enough
            val maxCompressedSize = TJ.bufSize(width, height, TJ.SAMP_444)

            // Bitmap pixels
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            // Convert the bimap to ARGB bytearray cause of I used pixel format == `PF_ARGB`
            val srcArray = bitmap.toARGB()

            val jpegBuf = ByteArray(maxCompressedSize)
            val tjCompressor = TJCompressor()
            tjCompressor.setSourceImage(
                srcArray,
                0,
                0,
                width,
                width * 4,
                height,
                TJ.PF_ARGB
            )
            // Set the image quality (1-100)
            tjCompressor.setJPEGQuality(quality)
            tjCompressor.setSubsamp(TJ.SAMP_444)
            tjCompressor.compress(jpegBuf, TJ.FLAG_FASTDCT)
            jpegBuf.toBitmap()
        }

    /**
     * To ARGB [toARGB]
     * - This will converts [Bitmap] to [ByteArray] argb formatted.
     * This will depends on your pixel format == [TJ.PF_ARGB]
     * @return
     */
    private fun Bitmap.toARGB(): ByteArray {
        val buffer = IntArray(width * height)
        // Get pixel data from the Bitmap and store it in buffer
        getPixels(buffer, 0, width, 0, 0, width, height)
        // Create a new byte array to hold the ARGB data
        val bytes = ByteArray(width * height * 4)
        // Iterate through each pixel in the buffer
        for (i in 0 until width * height) {
            val pixel = buffer[i]
            // Extract the ARGB values from the pixel
            val alpha = Color.alpha(pixel)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            // Write the ARGB values to the byte array
            // A - Alpha
            bytes[i * 4] = alpha.toByte()
            // R - Red
            bytes[i * 4 + 1] = red.toByte()
            // G - Green
            bytes[i * 4 + 2] = green.toByte()
            // B - Blue
            bytes[i * 4 + 3] = blue.toByte()
        }
        return bytes
    }

    /**
     * To bitmap [toBitmap]
     * - Convert the [ByteArray] to [Bitmap]
     * @return [Bitmap]
     */
    private fun ByteArray.toBitmap(): Bitmap = run { BitmapFactory.decodeByteArray(this, 0, size) }

    private fun Bitmap.saveImage(): File = run {
        // generate a unique filename
        val filename = "turbo_jpeg_${System.currentTimeMillis()}.jpg"

        // create a File object with the file path and filename
        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filename)
        Log.d(TAG, "Saved in ${file.absolutePath}")

        // create a FileOutputStream object and pass the File object as parameter
        val fos = FileOutputStream(file)

        // compress the bitmap and write it to the output stream
        compress(Bitmap.CompressFormat.JPEG, 100, fos)

        // close the output stream
        fos.close()

        return@run file
    }
}