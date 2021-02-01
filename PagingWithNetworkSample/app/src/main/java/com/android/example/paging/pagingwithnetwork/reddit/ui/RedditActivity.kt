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

package com.android.example.paging.pagingwithnetwork.reddit.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.example.paging.pagingwithnetwork.FragmentStateAdapter
import com.android.example.paging.pagingwithnetwork.R
import com.android.example.paging.pagingwithnetwork.reddit.repository.RedditPostRepository
import kotlinx.android.synthetic.main.activity_reddit.*

/**
 * A list activity that shows reddit posts in the given sub-reddit.
 * <p>
 * The intent arguments can be modified to make it use a different repository (see MainActivity).
 */
class RedditActivity : AppCompatActivity() {
    companion object {
        const val KEY_REPOSITORY_TYPE = "repository_type"
        fun intentFor(context: Context, type: RedditPostRepository.Type): Intent {
            val intent = Intent(context, RedditActivity::class.java)
            intent.putExtra(KEY_REPOSITORY_TYPE, type.ordinal)
            return intent
        }
    }

    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reddit)

        tabLayout.setupWithViewPager(viewPager)

        val fragmentStateAdapter = FragmentStateAdapter<RedditFragment>(supportFragmentManager)

        viewPager.adapter = fragmentStateAdapter


        fragmentStateAdapter.setFragments((1..5).map { catalogEntity ->
            RedditFragment()
        })
        fragmentStateAdapter.setTitles((1..5).map { entity -> "entity $entity" })

        fragmentStateAdapter.notifyDataSetChanged()
    }
}
