package com.ray.networkapt

import com.google.gson.annotations.SerializedName

class BaseHttpResult<T>(
    var code: Int = -1,
    @SerializedName("msg") var message: String = "",
    var data: T? = null
)