/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.example.data.db


import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Main database description.
 */
@Database(
    entities = [
        com.android.example.model.User::class,
        com.android.example.model.Repo::class,
        com.android.example.model.Contributor::class,
        com.android.example.data.db.RepoSearchResult::class],
    version = 3,
    exportSchema = false
)
abstract class GithubDb : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao
}
