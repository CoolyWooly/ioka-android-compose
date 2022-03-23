package kz.ioka.android.iokademoapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kz.ioka.android.iokademoapp.data.*
import kz.ioka.android.iokademoapp.data.local.ProfileDao
import kz.ioka.android.iokademoapp.data.remote.DemoApi

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoriesModule {

    @Provides
    fun provideCustomerRepository(
        profileDao: ProfileDao
    ): CustomerRepository {
        return CustomerRepositoryImpl(profileDao)
    }

    @Provides
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
        demoApi: DemoApi,
        profileDao: ProfileDao
    ): SettingsRepository {
        return SettingsRepositoryImpl(context, demoApi, profileDao)
    }

    @Provides
    fun provideOrderRepository(
        demoApi: DemoApi,
        profileDao: ProfileDao
    ): OrderRepository {
        return OrderRepositoryImpl(demoApi, profileDao)
    }

}