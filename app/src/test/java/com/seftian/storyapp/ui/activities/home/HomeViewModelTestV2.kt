package com.seftian.storyapp.ui.activities.home

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.model.StoryResponse
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.domain.StoryRepository
import com.seftian.storyapp.ui.activities.home.adapter.StoryAdapter
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTestV2 {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @MockK
    private lateinit var repository: StoryRepository
    private lateinit var userDatabase: UserDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notesApi: NotesApi
    private lateinit var homeViewModel: HomeViewModel

    private val dummyStories = Dummy.generateDataDummy()
    private val data: PagingData<StoryResponse> = QuotePagingSource.snapshot(dummyStories)
    private val expectedStories = MutableLiveData<PagingData<StoryResponse>>()

    @Before
    fun setup() {
        MockKAnnotations.init(this)


        userDatabase = mockk()
        sharedPreferences = mockk()
        notesApi = mockk()

        repository = mockk{
            expectedStories.value = data
            every { this@mockk.getStory() } returns expectedStories
        }

        homeViewModel = HomeViewModel(
            userDatabase,
            sharedPreferences,
            repository
        )

    }

    @Test
    fun `when Get Stories Should Not Null`() = runTest {

        val actualStories: PagingData<StoryResponse> = homeViewModel.userStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())

    }

    @Test
    fun `when Get Stories Should Match The Length`() = runTest {

        val actualStories: PagingData<StoryResponse> = homeViewModel.userStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertEquals(dummyStories.size, differ.snapshot().size)
    }


    @Test
    fun `when Get Stories The First Item Should Match`() = runTest {

        val actualStories: PagingData<StoryResponse> = homeViewModel.userStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertEquals(dummyStories[0], differ.snapshot()[0])
    }


    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {

        val emptyStory: PagingData<StoryResponse> = PagingData.from(emptyList())
        expectedStories.value = emptyStory

        val actualStories: PagingData<StoryResponse> = homeViewModel.userStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class QuotePagingSource : PagingSource<Int, LiveData<List<StoryResponse>>>() {
        companion object {
            fun snapshot(items: List<StoryResponse>): PagingData<StoryResponse> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryResponse>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryResponse>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}