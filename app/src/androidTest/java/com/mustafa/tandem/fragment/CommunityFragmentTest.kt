package com.mustafa.tandem.fragment

import MockTestUtil
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.tandem.R
import com.mustafa.tandem.binding.FragmentBindingAdapters
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.Resource
import com.mustafa.tandem.util.*
import com.mustafa.tandem.view.CommunityFragment
import com.mustafa.tandem.view.CommunityViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class CommunityFragmentTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()


    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var viewModel: CommunityViewModel
    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )
    private val results = MutableLiveData<Resource<List<Member>>>()

    @Before
    fun init() {
        viewModel = mock()
        whenever(viewModel.membersListLiveData).thenReturn(results)

        mockBindingAdapter = Mockito.mock(FragmentBindingAdapters::class.java)

        val scenario = launchFragmentInContainer(themeResId = R.style.AppTheme) {
            CommunityFragment().apply {
                viewModelFactory = ViewModelUtil.createFor(viewModel)
                dataBindingComponent = object : DataBindingComponent {
                    override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                        return mockBindingAdapter
                    }
                }
            }
        }
        dataBindingIdlingResourceRule.monitorFragment(scenario)

        // Set the navigation graph to the NavHostController
        navController.setGraph(R.navigation.main)

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
            fragment.disableProgressBarAnimations()
        }
    }

    @Test
    fun testBasics_ProgressBar() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))

        results.postValue(Resource.loading())

        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testErrorResult_RetryButton_SnackBar() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.button_retry))
            .check(matches(not(isDisplayed())))

        results.postValue(Resource.error("ERROR"))

        onView(withId(R.id.button_retry))
            .check(matches(isDisplayed()))

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("ERROR")))
    }

    @Test
    fun loadCommunityMembers() {

        //Given
        val oldMemberList = MockTestUtil.createMembers(3)

        // Action
        results.postValue(Resource.success(oldMemberList, true))

        // Assert
        onView(withId(R.id.members_recycler_view)).check(matches(isDisplayed()))

        onView(listMatcher().atPosition(0)).check(matches(isDisplayed()))
        onView(listMatcher().atPosition(1)).check(matches(isDisplayed()))
        onView(listMatcher().atPosition(2)).check(matches(isDisplayed()))

        //if your ViewHolder uses ViewGroup, wrap withText() with a hasDescendant() like:
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("Mustafa0"))))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("DE0"))))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("EN0"))))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("TOPIC0"))))

        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("Mustafa1"))))
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("DE1"))))
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("EN1"))))
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("TOPIC1"))))

        onView(listMatcher().atPosition(2)).check(matches(hasDescendant(withText("Mustafa2"))))
        onView(listMatcher().atPosition(2)).check(matches(hasDescendant(withText("DE2"))))
        onView(listMatcher().atPosition(2)).check(matches(hasDescendant(withText("EN2"))))
        onView(listMatcher().atPosition(2)).check(matches(hasDescendant(withText("TOPIC2"))))
    }

    /**
     * The new member is a member with cntReference = 0 having an image written on it NEW , The old member with cntReference > 1
     */
    @Test
    fun simpleNewAndOldForNewMembers() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))

        val newMember = Member("Mustafa", emptyList(), emptyList(), "URL", 0, "TOPIC")
        val oldMember = Member("Mustafa", emptyList(), emptyList(), "URL", 10, "TOPIC")

        results.postValue(Resource.success(listOf(newMember, oldMember), true))

        onView(withId(R.id.members_recycler_view)).check(matches(isDisplayed()))

        onView(listMatcher().atPosition(0)).check(matches(isDisplayed()))
        onView(listMatcher().atPosition(1)).check(matches(isDisplayed()))

        onView(listMatcher().atPositionOnView(0, R.id.new_icon)).check(matches(isDisplayed()))
        onView(listMatcher().atPositionOnView(0, R.id.count_ref)).check(matches(not(isDisplayed())))

        onView(listMatcher().atPositionOnView(1, R.id.new_icon)).check(matches(not(isDisplayed())))
        onView(listMatcher().atPositionOnView(1, R.id.count_ref)).check(matches(isDisplayed()))

    }

    @Test
    fun loadMore_IsLastPage_No() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))

        val oldMemberList = MockTestUtil.createMembers(20)
        results.postValue(Resource.success(oldMemberList, false))

        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.members_recycler_view)).perform(action)

        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()))

        verify(viewModel).loadMore()

    }

    @Test
    fun loadMore_IsLastPage_Yes() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))

        val oldMemberList = MockTestUtil.createMembers(19)
        results.postValue(Resource.success(oldMemberList, true))

        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(18)
        onView(withId(R.id.members_recycler_view)).perform(action)

        onView(listMatcher().atPosition(18)).check(matches(isDisplayed()))

        verify(viewModel, never()).loadMore()

    }


    @Test
    fun testErrorResult_RetryButton_Action() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.button_retry))
            .check(matches(not(isDisplayed())))

        results.postValue(Resource.error("ERROR"))

        onView(withId(R.id.button_retry))
            .check(matches(isDisplayed()))

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("ERROR")))
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.members_recycler_view)
    }
}