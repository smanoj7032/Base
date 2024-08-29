package com.manoj.base.core.di


import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.dispatchers.DispatchersProviderImpl
import com.manoj.base.data.repository.BaseRepoImpl
import com.manoj.base.domain.repositary.BaseRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    @Singleton
    abstract fun bindDispatchersProvider(dispatchersProviderImpl: DispatchersProviderImpl): DispatchersProvider

    @Binds
    @Singleton
    abstract fun bindPostRepository(postRepositoryImp: BaseRepoImpl): BaseRepo
}