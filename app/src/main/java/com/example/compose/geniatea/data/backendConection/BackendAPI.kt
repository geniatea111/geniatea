package com.example.compose.geniatea.data.backendConection

import android.content.Context
import com.example.compose.geniatea.data.StoreDataUser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object BackendAPI {
    const val BASE_URL_MOCKROON = "http://192.168.18.121:3000/"
    const val BASE_URL_LOCAL = "http://192.168.18.121:8080/"
    const val BASE_URL_LOCAL2 = "http://192.168.137.1:8080/"

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private lateinit var client: OkHttpClient
    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context, StoreDataUser()))
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_LOCAL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    val retrofitService: ApiService by lazy {
        if (!::retrofit.isInitialized) {
            throw IllegalStateException("BackendAPI no inicializado. Llama a BackendAPI.init(context) primero.")
        }
        retrofit.create(ApiService::class.java)
    }
}


/*
object BackendAPI {
    val BASE_URL_MOCKROON = "http://192.168.18.121:3000/"

    val BASE_URL_LOCAL = "http://192.168.18.121:8080/" //ethernet

    val BASE_URL_LOCAL2 = "http://192.168.137.1:8080/" //wifi

    val logging = HttpLoggingInterceptor()
    init {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    val context =

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor(context, StoreDataUser()))
        .build()

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .baseUrl(BASE_URL_LOCAL)
        .build()

    val retrofitService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}*/