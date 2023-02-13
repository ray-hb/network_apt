# network_apt
apt生成部分网络请求代码

## 辅助生成retrofit网络请求具体实现

### 1.集成方式

在子模块的build.gradle中导入:

```gradle
    //use remote apt_annotations
    //implementation project(':apt_annotations')
    implementation 'com.ray.module:repository_annotation:1.0'

    //use remote http-network
    //implementation project(':http_network')
    implementation('com.ray.module:network:1.0')

    kapt 'com.ray.module:repository-processor:1.0'
    //kapt project(':apt_processor')
```

> maven地址：maven { url("https://gitee.com/android-ray/self-maven/raw/master") }

### 使用方式：
```kotlin
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
```
在retrofit的 interface上面添加ServiceRepository注解，altName可以改生产retrofit实现类的名称

效果如下：
```kotlin
public class NfcRepository {
  public suspend fun getCurrentVersionBean(
    currentVersion: String,
    age: Int,
    argus: Map<String, String>,
  ): BaseHttpResult<VersionBean> =
      createService(INfcService::class.java).getCurrentVersionBean(currentVersion,age,argus)

  public suspend fun getNfc(): BaseHttpResult<Boolean> =
      createService(INfcService::class.java).getNfc()

  public suspend fun getNfc1(): BaseHttpResult<List<Int>> =
      createService(INfcService::class.java).getNfc1()
}
```
>  返回值可以任意定

