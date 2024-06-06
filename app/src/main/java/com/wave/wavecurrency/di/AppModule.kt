package com.wave.wavecurrency.di

import com.wave.wavecurrency.network.APIService
import com.wave.wavecurrency.network.AccessKeyInterceptor
import com.wave.wavecurrency.repository.ExchangeRatesRepository
import com.wave.wavecurrency.repository.ExchangeRatesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAccessKeyInterceptor(): AccessKeyInterceptor {
        val accessKey = "ffa85d5766ced52efd76f836a51e4511"
        return AccessKeyInterceptor(accessKey)
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        accessKeyInterceptor: AccessKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Add logging interceptor
        builder.addInterceptor(loggingInterceptor)

        // Add access key interceptor
        builder.addInterceptor(accessKeyInterceptor)

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideAPIService(httpClient: OkHttpClient): APIService {
        return Retrofit.Builder()
            .baseUrl("http://apilayer.net/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(
        apiService: APIService
    ): ExchangeRatesRepository {
        return ExchangeRatesRepositoryImpl(apiService)
    }

}
