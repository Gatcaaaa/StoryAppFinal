package com.submisson.aleggappstory.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.submisson.aleggappstory.DataDummy
import com.submisson.aleggappstory.LiveDataTestUtil.getOrAwaitValue
import com.submisson.aleggappstory.MainDispatcherRule
import com.submisson.aleggappstory.data.UserRepository
import com.submisson.aleggappstory.data.pref.UserModel
import com.submisson.aleggappstory.data.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: UserRepository
    private val dummyStories = DataDummy.generateDummyStoryResponse()
    private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTJJVHVOeEVoVGNqUVhvR2MiLCJpYXQiOjE2OTg0NzU5MDF9.zCaBrnCD1XnlucrosuoHqvq1BK0wcvFBl2HmCX5LY94"

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val data : PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(repository.getStories(dummyToken)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(repository)
        val actualMainStory: PagingData<ListStoryItem> = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallBack,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualMainStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data : PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        Mockito.`when`(repository.getStories(dummyToken)).thenReturn(expectedStory)

        val mainViewModel = MainViewModel(repository)
        val actualMainStory: PagingData<ListStoryItem> = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallBack,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualMainStory)

        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource: PagingSource<Int, LiveData<ListStoryItem>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<ListStoryItem>>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<ListStoryItem>> {
        return LoadResult.Page(emptyList(),0,1)
    }

    companion object {
        fun snapshot(item: List<ListStoryItem>):PagingData<ListStoryItem>{
            return PagingData.from(item)
        }
    }
}

val noopListUpdateCallBack = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {

    }

    override fun onRemoved(position: Int, count: Int) {

    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {

    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {

    }
}