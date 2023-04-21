package ru.netology.nmedia.di

import dagger.Module
import dagger.Provides
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GoogleApiAvailabilityModule {
    @Provides
    @Singleton
    fun provideGoogleApiAvailability(): GoogleApiAvailability {
        return GoogleApiAvailability()
    }
}