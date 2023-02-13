package com.ray.networkapt

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.ray.network.HttpCallResult
import com.ray.network.http.HttpConfig
import com.ray.network.httpConfig
import com.ray.network.triggerHttpCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : CoroutineScope, AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        httpConfig(HttpConfig(baseUrl = "https://eboard.leke.cn/api/"))

        findViewById<Button>(R.id.bt_button).setOnClickListener {
            val repository = UpdateServiceRepository()
            launch {
                val result = triggerHttpCall { repository.loginDevice("pr1111", "111111", "test1") }
                if (result is HttpCallResult.Success) {
                    if (result.data.data != null) {
                        Log.e("TAG", "${result.data.data?.userInfo?.userId}")
                    } else {
                        Log.e("tag", "code = ${result.data.code},msg = ${result.data.message}")
                    }
                } else {
                    Log.e("TAG", "${(result as HttpCallResult.Error).netException.msg}")
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main
}