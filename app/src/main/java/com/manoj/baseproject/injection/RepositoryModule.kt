package com.manoj.baseproject.injection



import com.manoj.baseproject.data.repositary.BaseRepo
import com.manoj.baseproject.data.repositary.BaseRepoImpl
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