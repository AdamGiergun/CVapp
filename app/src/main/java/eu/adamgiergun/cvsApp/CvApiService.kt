package eu.adamgiergun.cvsApp

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://gist.githubusercontent.com/AdamGiergun/4420d8c879e6f73783e401c5ff1555d3/raw/ad8353a2129f267cf20764293c03f8d4e3a0eba5/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

internal interface CvApiService {
    @GET("cv.json")
    fun getCv(): Call<List<CvItem>>
}

internal object CvApi {
    val retrofitService: CvApiService by lazy {
        retrofit.create(CvApiService::class.java)
    }
}