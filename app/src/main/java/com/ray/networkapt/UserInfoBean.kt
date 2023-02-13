package com.ray.networkapt

import com.google.gson.annotations.SerializedName

class UserInfoBean {
    @SerializedName("area_id")
    var areaId: String? = null

    @SerializedName("card_id")
    var cardId: String? = null

    @SerializedName("class_id")
    var classId: String? = null

    @SerializedName("createtime")
    var createTime: String? = null

    @SerializedName("expires_in")
    var expiresIn: String? = null

    @SerializedName("expiretime")
    var expireTime: String? = null

    @SerializedName("nickname")
    var nickName: String? = null

    @SerializedName("school_id")
    var schoolId: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("user_id")
    var userId: String? = null

    @SerializedName("username")
    var userName: String? = null

    override fun toString(): String {
        return "DeviceLoginInfoBean(areaId=$areaId, cardId=$cardId, classId=$classId, createTime=$createTime, expiresIn=$expiresIn, expireTime=$expireTime, nickName=$nickName, schoolId=$schoolId, token=$token, userId=$userId, userName=$userName)"
    }
}