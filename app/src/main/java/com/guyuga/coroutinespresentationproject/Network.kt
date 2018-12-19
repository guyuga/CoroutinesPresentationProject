package com.guyuga.coroutinespresentationproject

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

/**
 * Created by Guy Lidan on 17/12/2018.
 */
class Network {
    companion object {
        private const val IMAGE_URL = "https://blog.bugsnag.com/img/feature/kotlin.png"
        const val KEY = "https://jsonplaceholder.typicode.com/todos/"
        fun getUrl(code: Int) = KEY + code
    }

    suspend fun callService(service: String) : String {
        return withContext(Dispatchers.IO) {
            URL(service).readText()
        }
    }

    suspend fun parseJason(response: String) :JSONObject {
        return withContext(Dispatchers.Default) {
            JSONObject(response)
        }
    }

    suspend fun getStringFromJson(response: JSONObject, key: String) : String {
        return response.getString(key)
    }

    fun callService(service: String, delegate: (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            delegate(URL(service).readText())
        }
    }

    fun parseJason(response: String, delegate: (JSONObject) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            delegate(JSONObject(response))
        }
    }

    fun getStringFromJson(response: JSONObject, key: String, delegate: (String) -> Unit) {
        delegate(response.getString(key))
    }
}