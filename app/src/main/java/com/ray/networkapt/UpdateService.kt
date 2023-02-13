package com.ray.networkapt

import com.cnstrong.annotations.ServiceRepository
import okhttp3.MultipartBody
import retrofit2.http.*

@ServiceRepository
interface UpdateService {

    @GET("https://www.baidu.com/xxx")
    suspend fun getCurrentVersionBean(
        currentVersion: String,
        age: Int,
        argus: Map<String, String>
    ): BaseHttpResult<VersionBean>

    suspend fun getEboardSetting(): BaseHttpResult<Any>

    @FormUrlEncoded
    @POST("https://www.baidu.com/xxx")
    fun getCardSetting(a: Int, argus:Array<VersionBean>): String


    @FormUrlEncoded
    @POST("https://webapp.leke.cn/api/user/login")
    suspend fun loginDevice(
        @Field("account") account: String,
        @Field("password") password: String,
        @Field("mac") mac: String
    ): BaseHttpResult<DeviceLoginInfoBean>

    /**
     * 同步文件上传的路径到后端
     * @param userId 用户id
     * @param logUrl 日志路径
     */
    @POST("http://phpapilts.leke.cn:9501/api/client/log")
    @Headers("Content-Type:application/json")
    suspend fun syncSendLogContent(
        @Body info: UploadSchoolLogInfoBean,
        @Query("ticket") ticket: String
    ): BaseHttpResult<Any>

    @Multipart
    @POST("https://fs.leke.cn/api/w/upload/file/binary.htm")
    suspend fun syncUploadLogZip(@Part part: MultipartBody.Part, @Query("ticket") ticket: String):BaseHttpResult<UploadSchoolLogInfoBean>
}

class UploadSchoolLogInfoBean {

}
