package com.ray.networkapt

import com.cnstrong.annotations.ServiceRepository
import retrofit2.http.GET

@ServiceRepository(altName = "NfcRepository")
interface INfcService {

    @GET("https://www.baidu.com/xxx")
    suspend fun getCurrentVersionBean(
        currentVersion: String,
        age: Int,
        argus: Map<String, String>
    ): BaseHttpResult<VersionBean>


    suspend fun getNfc(): BaseHttpResult<Boolean>

    suspend fun getNfc1(): BaseHttpResult<List<Int>>
}