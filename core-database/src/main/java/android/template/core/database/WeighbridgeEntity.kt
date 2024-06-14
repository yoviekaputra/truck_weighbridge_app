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

package android.template.core.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = WeighbridgeEntity.TABLE_NAME
)
data class WeighbridgeEntity(
    @ColumnInfo(defaultValue = "0")
    val datetime: Long,

    @ColumnInfo(defaultValue = "")
    val licenceNumber: String,

    @ColumnInfo(defaultValue = "")
    val driverName: String,

    @ColumnInfo(defaultValue = "0")
    val inboundWeight: Double,

    @ColumnInfo(defaultValue = "0")
    val outboundWeight: Double
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    companion object {
        const val TABLE_NAME = "weighbridge"
    }
}

@Dao
interface MyModelDao {

    @Query("SELECT * FROM weighbridge WHERE uid = :id")
    fun get(id: Int): Flow<WeighbridgeEntity>

    @Query("SELECT * FROM weighbridge ORDER BY uid ASC LIMIT 10")
    fun getAsc(): Flow<List<WeighbridgeEntity>>

    @Query("SELECT * FROM weighbridge ORDER BY uid DESC LIMIT 10")
    fun getDesc(): Flow<List<WeighbridgeEntity>>

    @Query(
        "SELECT * FROM weighbridge " +
                "WHERE (licenceNumber LIKE :query || '%' OR driverName LIKE :query || '%')" +
                "ORDER BY uid ASC"
    )
    fun getByQueryOrderUidAsc(query: String): Flow<List<WeighbridgeEntity>>

    @Query(
        "SELECT * FROM weighbridge " +
                "WHERE (licenceNumber LIKE :query || '%' OR driverName LIKE :query || '%')" +
                "ORDER BY uid DESC"
    )
    fun getByQueryOrderUidDesc(query: String): Flow<List<WeighbridgeEntity>>

    @Insert
    suspend fun insertMyModel(item: WeighbridgeEntity)

    @Delete
    suspend fun delete(item: WeighbridgeEntity)
}
