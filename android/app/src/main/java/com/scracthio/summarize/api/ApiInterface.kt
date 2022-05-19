package com.scracthio.summarize.api

import com.scracthio.summarize.Text
import org.json.JSONObject


interface ApiInterface {
    fun onSuccess(resultCode: Int, summary: Text);
    fun onFailure(resultCode: Int);

}