package kz.ioka.android.iokademoapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kz.ioka.android.iokademoapp.data.local.ProfileDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    @Singleton
    fun provideProfileDao(): ProfileDao {
        return ProfileDao()
    }

}