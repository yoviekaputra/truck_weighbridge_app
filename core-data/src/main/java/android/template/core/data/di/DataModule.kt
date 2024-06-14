/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.core.data.di

import android.template.core.data.DefaultMyModelRepository
import android.template.core.data.MyModelRepository
import android.template.core.data.models.WeighbridgeData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsMyModelRepository(
        myModelRepository: DefaultMyModelRepository
    ): MyModelRepository
}

class FakeMyModelRepository @Inject constructor() : MyModelRepository {
    override fun get(id: Int): Flow<WeighbridgeData> {
        throw NotImplementedError()
    }

    override fun get(query: String, sortByAscending: Boolean): Flow<List<WeighbridgeData>> {
        return flowOf(fakeMyModels)
    }

    override suspend fun add(data: WeighbridgeData) {
        throw NotImplementedError()
    }

    override suspend fun update(data: WeighbridgeData) {
        throw NotImplementedError()
    }

    override suspend fun delete(data: WeighbridgeData) {
        throw NotImplementedError()
    }
}

val fakeMyModels = (0..3).map {
    WeighbridgeData(
        datetime = System.currentTimeMillis() + it,
        driverName = "Driver $it",
        licenceNumber = "BA $it",
        inboundWeight = it * 10.0,
        outboundWeight = it * 15.0
    )
}
