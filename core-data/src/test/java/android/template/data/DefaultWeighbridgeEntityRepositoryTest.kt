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

package android.template.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import android.template.core.data.DefaultMyModelRepository
import android.template.core.data.models.WeighbridgeData
import android.template.core.database.WeighbridgeEntity
import android.template.core.database.MyModelDao

/**
 * Unit tests for [DefaultMyModelRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultWeighbridgeEntityRepositoryTest {

    @Test
    fun myModels_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultMyModelRepository(FakeMyModelDao())
        val data = WeighbridgeData(
            datetime = 0,
            licenceNumber = "B123",
            driverName = "",
            inboundWeight = 0.0,
            outboundWeight = 0.0
        )
        repository.add(data)

        assertEquals(repository.myModels.first().size, 1)
    }

}

private class FakeMyModelDao : MyModelDao {

    private val data = mutableListOf<WeighbridgeEntity>()

    override fun getMyModels(): Flow<List<WeighbridgeEntity>> = flow {
        emit(data)
    }

    override suspend fun insertMyModel(item: WeighbridgeEntity) {
        data.add(0, item)
    }
}