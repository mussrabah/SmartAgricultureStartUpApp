package com.muss_coding.smartagriculturestartupapp.di

import com.muss_coding.smartagriculturestartupapp.onboarding.domain.use_case.GetDateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGetDateUseCase(): GetDateUseCase {
        return GetDateUseCase()
    }
}