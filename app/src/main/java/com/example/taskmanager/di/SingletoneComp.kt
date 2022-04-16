package com.example.taskmanager.di

import android.app.Application
import com.example.taskmanager.data.repositories.ProjectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProviderModule {
    @Provides
    fun provideProjectRepository(application: Application): ProjectRepository {
        return ProjectRepository(application)
    }
}