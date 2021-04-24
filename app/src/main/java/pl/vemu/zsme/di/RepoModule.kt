package pl.vemu.zsme.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import pl.vemu.zsme.db.PostDAO
import pl.vemu.zsme.mappers.PostMapper
import pl.vemu.zsme.repo.NewsRepo
import pl.vemu.zsme.service.ZSMEService

@InstallIn(ViewModelComponent::class)
@Module
class RepoModule {

    @Provides
    @ViewModelScoped
    fun provideNewsRepo(
            postDAO: PostDAO,
            zsmeService: ZSMEService,
            postMapper: PostMapper,
    ) = NewsRepo(postDAO, zsmeService, postMapper)
    // TODO

}