package com.scracthio.summarize.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.scracthio.summarize.BuildConfig
import com.scracthio.summarize.Text
import com.scracthio.summarize.api.ApiClass
import com.scracthio.summarize.api.ApiInterface
import com.scracthio.summarize.databinding.ActivitySourceBinding
import org.json.JSONObject


class SourceActivity : Activity()
{
    private lateinit var mediaUri: Uri
    private lateinit var sourceBinding: ActivitySourceBinding
    private lateinit var apiClass: ApiClass
    private val mediaIntentRequestCode = 1
    private val mediaContent = false
    private lateinit var part_file: String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        apiClass = ApiClass()
        sourceBinding = ActivitySourceBinding.inflate(layoutInflater)
        setContentView(sourceBinding.root)

        sourceBinding.selectMediaButton.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_PICK).apply {
                type = "audio/*"
            }
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*", "audio/*"))
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, mediaIntentRequestCode)
            }
            val s: String = sourceBinding.transcriptInputText.text.toString()
            Log.d("summary", s)
        }

        newState()
    }

    private fun getTextSummary() {
        sourceBinding.media.text = "Waiting for server response"
        apiClass.getSummary(
            text = sourceBinding.transcriptInputText.text.toString(),
            onComplete = object: ApiInterface {
                override fun onSuccess(resultCode: Int, summary: Text) {
                    summaryState(summary.text)
                }

                override fun onFailure(resultCode: Int) {
                    Toast.makeText(this@SourceActivity, "failed getting audio", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun getMediaSummary(part_file: String, ext: String) {
        sourceBinding.media.text = "Waiting for server response"
        apiClass.getSummaryFromFile(
            part_file,
            ext,
            object: ApiInterface {
                override fun onSuccess(resultCode: Int, summary: Text) {
                    summaryState(summary.text)
                    Toast.makeText(this@SourceActivity, "upload successful", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(resultCode: Int) {
                    Toast.makeText(this@SourceActivity, "upload unsuccessful", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    fun summaryState(text: String) {
        sourceBinding.divider.visibility = View.GONE
        sourceBinding.selectMediaButton.visibility = View.GONE
        Log.d("Source", text)
        sourceBinding.media.text = "Summary"
        sourceBinding.summary.text = text
        sourceBinding.summary.visibility = View.VISIBLE
        sourceBinding.summarizeButton.text = "Clear"
        sourceBinding.summarizeButton.setOnClickListener {
            newState()
        }
    }

    private fun newState() {
        sourceBinding.divider.visibility = View.VISIBLE
        sourceBinding.selectMediaButton.visibility = View.VISIBLE
        sourceBinding.summary.visibility = View.GONE
        sourceBinding.summarizeButton.text = "Summarize"
        sourceBinding.media.text = ""
        sourceBinding.summarizeButton.setOnClickListener {
            getTextSummary()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mediaIntentRequestCode && resultCode == RESULT_OK) {
            val selectedFile = data!!.data // Get the image file URI
            val imageProjection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                selectedFile?.let { contentResolver.query(it, imageProjection, null, null, null) }
            if (cursor != null) {
                cursor.moveToFirst()
                val indexImage: Int = cursor.getColumnIndex(imageProjection[0])
                part_file = cursor.getString(indexImage)
                sourceBinding.media.text = "Media selected"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                    Snackbar.make(sourceBinding.root, "Storage permission required", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Allow") {
                            try {
                                val uri: Uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                                val intent: Intent =
                                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                                startActivity(intent)
                            }
                            catch (e: Exception) {
                                val intent: Intent = Intent()
                                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                                startActivity(intent)
                            }
                        }
                        .show()
                }
                sourceBinding.summarizeButton.setOnClickListener {
                    getMediaSummary(part_file, "mp3")
                }
            }
        }
    }
}

