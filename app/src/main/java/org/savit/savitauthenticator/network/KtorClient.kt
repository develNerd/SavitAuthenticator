package org.savit.savitauthenticator.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class KtorClient(networkConnectionInterceptor: NetworkConnectionInterceptor) {

    val httpClient = HttpClient(OkHttp){
        engine {
            addInterceptor(networkConnectionInterceptor)
            addNetworkInterceptor(networkConnectionInterceptor)
        }
        install(HttpTimeout){
            requestTimeoutMillis = 10000
        }
        install(JsonFeature){
            serializer = GsonSerializer()
        }
        install(Logging){
            logger = object :Logger{
                override fun log(message: String) {
                    Log.v("Ktor Android Client =>", message)
                }
            }
            level = LogLevel.ALL
        }
    }

    suspend fun getDateHeader():HttpResponse{
        val response:HttpResponse = httpClient.get("https://www.google.com")
        Log.d("TimeStamp",response.responseTime.timestamp.toString())
        return response
    }


}