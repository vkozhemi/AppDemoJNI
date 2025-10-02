package com.vkozhemi.appdemo.di

import android.app.Application
import com.vkozhemi.appdemo.data.ImagesRepository
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
    fun provideImagesRepository(application: Application): ImagesRepository =
        ImagesRepository(application)
}
