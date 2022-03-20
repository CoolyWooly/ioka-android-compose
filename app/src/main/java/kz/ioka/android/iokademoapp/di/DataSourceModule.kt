package kz.ioka.android.iokademoapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kz.ioka.android.ioka.api.dataSource.IokaDataSource
import kz.ioka.android.ioka.api.dataSource.IokaDataSourceImpl

@Module
@InstallIn(ViewModelComponent::class)
internal object DataSourceModule {

    @Provides
    fun provideIokaDataSource(): IokaDataSource {
        return IokaDataSourceImpl()
    }

}