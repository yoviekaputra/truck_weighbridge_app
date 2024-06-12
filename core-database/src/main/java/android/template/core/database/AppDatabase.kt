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

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [
        WeighbridgeEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = AppDatabase.RenameTableWeighbridge::class
        )
    ]
)
abstract class AppDatabase : RoomDatabase() {

    @RenameColumn(tableName = "MyModel", fromColumnName = "name", toColumnName = "driverName")
    @RenameTable(fromTableName = "MyModel", toTableName = WeighbridgeEntity.TABLE_NAME)
    class RenameTableWeighbridge : AutoMigrationSpec

    abstract fun myModelDao(): MyModelDao
}
