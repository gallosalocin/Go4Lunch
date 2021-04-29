package com.gallosalocin.go4lunch.data.repositories

import com.gallosalocin.go4lunch.data.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
        remoteDataSource: RemoteDataSource
) {

    val remote = remoteDataSource

}