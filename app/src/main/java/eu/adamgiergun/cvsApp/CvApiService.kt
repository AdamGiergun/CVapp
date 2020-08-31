package eu.adamgiergun.cvsApp

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://gist.githubusercontent.com/AdamGiergun/4420d8c879e6f73783e401c5ff1555d3/raw/650ba31e486d91def7a524cc204ab8050fbce3c9/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

internal interface CvApiService {
    @GET("cv.json")
    suspend fun getCv(): List<CvItem>
}

internal object CvApi {
    val retrofitService: CvApiService by lazy {
        retrofit.create(CvApiService::class.java)
    }
}