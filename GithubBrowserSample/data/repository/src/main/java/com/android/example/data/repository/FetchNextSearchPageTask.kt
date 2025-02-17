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

package com.android.example.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.example.data.api.ApiEmptyResponse
import com.android.example.data.api.ApiErrorResponse
import com.android.example.data.api.ApiResponse
import com.android.example.data.api.ApiSuccessResponse
import com.android.example.data.api.GithubService
import com.android.example.data.db.GithubDb
import com.android.example.data.db.RepoSearchResult
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
class FetchNextSearchPageTask constructor(
    private val query: String,
    private val githubService: GithubService,
    private val db: com.android.example.data.db.GithubDb
) : Runnable {
    private val _liveData = MutableLiveData<com.android.example.model.Resource<Boolean>>()
    val liveData: LiveData<com.android.example.model.Resource<Boolean>> = _liveData

    override fun run() {
        val current = db.repoDao().findSearchResult(query)
        if (current == null) {
            _liveData.postValue(null)
            return
        }
        val nextPage = current.next
        if (nextPage == null) {
            _liveData.postValue(com.android.example.model.Resource.success(false))
            return
        }
        val newValue = try {
            val response = githubService.searchRepos(query, nextPage).execute()
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    // we merge all repo ids into 1 list so that it is easier to fetch the
                    // result list.
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.repoIds)

                    ids.addAll(apiResponse.body.items.map { it.id })
                    val merged = com.android.example.data.db.RepoSearchResult(
                        query, ids,
                        apiResponse.body.total, apiResponse.nextPage
                    )
                    db.runInTransaction {
                        db.repoDao().insert(merged)
                        db.repoDao().insertRepos(apiResponse.body.items)
                    }
                    com.android.example.model.Resource.success(apiResponse.nextPage != null)
                }
                is ApiEmptyResponse -> {
                    com.android.example.model.Resource.success(false)
                }
                is ApiErrorResponse -> {
                    com.android.example.model.Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            com.android.example.model.Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
