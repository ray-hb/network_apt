package com.ray.networkapt

import com.google.gson.annotations.SerializedName

/**
 * @author tgw
 * @date 2021/8/24
 * @describe 设备登录返回
 */
class DeviceLoginInfoBean {

    @SerializedName("userinfo")
    var userInfo: UserInfoBean? = null

}

