package com.android.example.paging.pagingwithnetwork.reddit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.android.example.paging.pagingwithnetwork.GlideApp
import com.android.example.paging.pagingwithnetwork.R
import com.android.example.paging.pagingwithnetwork.reddit.ServiceLocator
import com.android.example.paging.pagingwithnetwork.reddit.repository.RedditPostRepository
import kotlinx.android.synthetic.main.fragment_reddit.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

class RedditFragment : Fragment() {


    private val model: SubRedditViewModel by viewModels {
        object : AbstractSavedStateViewModelFactory(this, null) {
            override fun <T : ViewModel?> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
            ): T {
                val repoTypeParam = RedditPostRepository.Type.DB.ordinal
                val repoType = RedditPostRepository.Type.values()[repoTypeParam]
                val repo = ServiceLocator.instance(requireContext())
                        .getRepository(repoType)
                @Suppress("UNCHECKED_CAST")
                return SubRedditViewModel(repo, handle) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reddit, container, false)
    }

    private lateinit var adapter: PostsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initAdapter()
        initSwipeToRefresh()
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        adapter = PostsAdapter(glide)
        list.adapter = adapter.withLoadStateHeaderAndFooter(
                header = PostsLoadStateAdapter(adapter),
                footer = PostsLoadStateAdapter(adapter)
        )

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                swipe_refresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        lifecycleScope.launchWhenCreated {
            model.posts.collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                    // Only emit when REFRESH LoadState for RemoteMediator changes.
                    .distinctUntilChangedBy { it.refresh }
                    // Only react to cases where Remote REFRESH completes i.e., NotLoading.
                    .filter { it.refresh is LoadState.NotLoading }
                    .collect { list.scrollToPosition(0) }
        }

        tv_refresh.setOnClickListener {
            model.showSubreddit("asdf")
        }
    }

    private fun initSwipeToRefresh() {
        swipe_refresh.setOnRefreshListener { adapter.refresh() }
    }
}