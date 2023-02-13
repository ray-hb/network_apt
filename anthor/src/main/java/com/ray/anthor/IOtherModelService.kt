package com.ray.anthor

import com.cnstrong.annotations.ServiceRepository

@ServiceRepository(altName = "OtherService")
interface IOtherModelService {

    suspend fun testGenerateService(params: String): String

}