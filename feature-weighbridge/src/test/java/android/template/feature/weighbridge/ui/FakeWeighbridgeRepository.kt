package android.template.feature.weighbridge.ui

import android.template.core.data.WeighbridgeRepository
import android.template.core.data.models.WeighbridgeData
import kotlinx.coroutines.flow.Flow

private class FakeWeighbridgeRepository : WeighbridgeRepository {

    private val data = mutableListOf<WeighbridgeData>()

    override fun get(id: Int): Flow<WeighbridgeData> {
        TODO("Not yet implemented")
    }

    override fun get(query: String, sortByAscending: Boolean): Flow<List<WeighbridgeData>> {
        TODO("Not yet implemented")
    }

    override suspend fun add(data: WeighbridgeData) {
        TODO("Not yet implemented")
    }

    override suspend fun update(data: WeighbridgeData) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: WeighbridgeData) {
        TODO("Not yet implemented")
    }
}
