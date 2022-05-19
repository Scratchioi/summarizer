//package com.scracthio.summarize;
//
//public class ffmpeg {
//
//
//    private fun execute() {
//        val process: Process = Runtime.getRuntime().exec("ffmpeg")
//        var line: String?
//                var x: String = ""
//        while (process.isAlive) {
//            val stdout: InputStream = process.inputStream
//            val osr = InputStreamReader(stdout)
//            val obr = BufferedReader(osr)
//            while (obr.readLine().also { line = it } != null) x += line
//        }
//        Log.i("execute block", x)
//        sourceBinding.videoConvert.ffmpegMessage.text = x
//    }
//
//    fun startConversion(mediaName: String) {
//        ffmpeg = FFmpeg.getInstance(this)
//        try {
//            ffmpeg.loadBinary(object : FFmpegLoadBinaryResponseHandler {
//                override fun onStart() {
//                    Toast.makeText(applicationContext, "started", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onFinish() {
//                    Toast.makeText(applicationContext, "ended", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onFailure() {
//                    Toast.makeText(applicationContext, "failed", Toast.LENGTH_SHORT).show()
//                    sourceBinding.videoConvert.ffmpegMessage.text = getString(R.string.ffmpeg_init_failed)
//                    showUnsupportedExceptionDialog()
//                }
//
//                override fun onSuccess() {
//                    sourceBinding.videoConvert.ffmpegMessage.text = getString(R.string.ready_for_conversion)
////                    val cmd: String = "-i ${getRealPathFromURI(applicationContext, mediaUri)} -acodec aac -strict -2 ${mediaName.split(".")[0]}.aac"
//                    val cmd = "-version"
//                    sourceBinding.videoConvert.ffmpegMessage.text = cmd
//                    sourceBinding.videoConvert.videoConvert.setOnClickListener {
//                        execute(cmd)
//                    }
//                }
//            })
//        } catch (e: FFmpegNotSupportedException) {
//            sourceBinding.mediaName.text = getString(R.string.ffmpeg_unsupported);
//        }
//    }
//
//    private fun execute(cmd: String) {
//        try {
//            ffmpeg.execute(cmd.split(" ").toTypedArray(), object : FFmpegExecuteResponseHandler {
//                override fun onStart() {
//                    sourceBinding.videoConvert.ffmpegMessage.text = "command: $cmd"
//                    Log.d(LOG_TAG, "execution of $cmd started")
//                }
//
//                override fun onFinish() {
//                    Log.d(LOG_TAG, "execution of $cmd ended")
//                }
//
//                override fun onSuccess(message: String?) {
//                    sourceBinding.videoConvert.ffmpegMessage.text = "Success with: $message"
//                }
//
//                override fun onProgress(message: String?) {
//                    sourceBinding.videoConvert.ffmpegMessage.text =
//                            "${sourceBinding.videoConvert.ffmpegMessage.text} \n $message"
//                }
//
//                override fun onFailure(message: String?) {
//                    sourceBinding.videoConvert.ffmpegMessage.text = "Failed with $message"
//                }
//            })
//        } catch (e: FFmpegCommandAlreadyRunningException) {
//            Log.d(LOG_TAG, e.message!!)
//        }
//    }
//
//    private fun showUnsupportedExceptionDialog() {
//        AlertDialog.Builder(this@SourceActivity)
//            .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("device not supported")
//                .setMessage("getString(R.string.device_not_supported_message)")
//                .setCancelable(false)
//                .setPositiveButton(android.R.string.ok,
//                        DialogInterface.OnClickListener { dialog, which -> this@SourceActivity.finish() })
//            .create()
//                .show()
//    }
//
//    private fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
//        var cursor: Cursor? = null
//        return try {
//            val proj = arrayOf(MediaStore.Images.Media.DATA)
//            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
//            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            cursor.getString(columnIndex)
//        } catch (e: Exception) {
//            Log.e(LOG_TAG, "getRealPathFromURI Exception : $e")
//            ""
//        } finally {
//            cursor?.close()
//        }
//    }
//
//    companion object
//    {
//        private const val MEDIA_INTENT_REQUEST_CODE = 0
//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
//        {
//            super.onActivityResult(requestCode, resultCode, data)
//            if (resultCode == RESULT_OK && requestCode == MEDIA_INTENT_REQUEST_CODE)
//            {
//                var mediaName: String? = null
//                mediaUri = data.data!!
//                    contentResolver.query(mediaUri, null, null, null, null)?.use { cursor ->
//                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                cursor.moveToFirst()
//                mediaName = cursor.getString(nameIndex)
//                sourceBinding.mediaName.text = mediaName
//            }
//                val mediaType = contentResolver.getType(mediaUri)!!.split("/")[0]
//                if (mediaType == "audio")
//                {
//                    //todo: generate transcript
//                }
//                if (mediaType == "video")
//                {
//                    sourceBinding.videoConvert.root.visibility = View.VISIBLE
////                startConversion(mediaName!!)
//                    sourceBinding.videoConvert.videoConvert.setOnClickListener { execute() }
//                }
//            }
//        }
//}
