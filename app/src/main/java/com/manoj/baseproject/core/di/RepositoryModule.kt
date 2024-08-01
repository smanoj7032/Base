package com.manoj.baseproject.core.di



import com.manoj.baseproject.domain.repositary.BaseRepo
import com.manoj.baseproject.data.repository.BaseRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun providesPostRepository(postRepositoryImp: BaseRepoImpl): BaseRepo =
        postRepositoryImp
}