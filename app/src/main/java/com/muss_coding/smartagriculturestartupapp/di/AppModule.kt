package com.muss_coding.smartagriculturestartupapp.di

import android.app.Application
import androidx.room.Room
import com.muss_coding.smartagriculturestartupapp.core.data.RepositoryImpl
import com.muss_coding.smartagriculturestartupapp.core.data.local.Database
import com.muss_coding.smartagriculturestartupapp.core.domain.repository.Repository
import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.TomorrowIoApi
import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.TomorrowIoApi.Companion.BASE_URL
import com.muss_coding.smartagriculturestartupapp.onboarding.data.remote.WeatherRemoteDataSource
import com.muss_coding.smartagriculturestartupapp.onboarding.data.repository.WeatherRepositoryImpl
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.repository.WeatherRepository
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case.GetMonitoringData
import com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case.UpdateControlUseCase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): Database {
        return Room.databaseBuilder(
            app,
            Database::class.java,
            "smart_agriculture_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: Database): Repository {
        return RepositoryImpl(db.dao)
    }
    @Provides
    @Singleton
    fun provideGetDateUseCase(repository: Repository): GetMonitoringData {
        return GetMonitoringData(repository)
    }
    @Provides
    @Singleton
    fun provideUpdateControlUseCase(repository: Repository): UpdateControlUseCase {
        return UpdateControlUseCase(repository)
    }

  //networking

    @Provides
    @Singleton
    fun provideWeatherRepository(
        remoteDataSource: WeatherRemoteDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(api: TomorrowIoApi): WeatherRemoteDataSource {
        return WeatherRemoteDataSource(api)
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideTomorrowIoApi(retrofit: Retrofit): TomorrowIoApi {
        return retrofit.create(TomorrowIoApi::class.java)
    }
}