package com.ray.network.http

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import net.sf.json.JSONObject
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class FixedGsonConverterFactory(private val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(): FixedGsonConverterFactory {
            return FixedGsonConverterFactory(Gson())
        }
    }

    override fun responseBodyConverter(
        type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return FixResponseBodyConverter(adapter)
    }
}

class FixResponseBodyConverter<T>(private val adapter: TypeAdapter<T>) :
    Converter<ResponseBody, T> {

    companion object {
        private const val TAG = "FixedGsonConverterFacto"
    }

    override fun convert(value: ResponseBody): T? {
        var responseStr = value.string()
        try {
            val jsonObject = JSONObject.fromObject(responseStr)
            val data = jsonObject.optJSONArray("data")
            if (data != null && data.size == 0) {
                jsonObject.remove("data");
                responseStr = jsonObject.toString();
            }
        } catch (e: Exception) {
            println("convert error")
        }
        try {
            return adapter.fromJson(responseStr)
        } finally {
            value.close();
        }
    }

}