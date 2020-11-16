# Tandem

* 100% Kotlin based + [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
* This app uses A single-activity pattern, using the Navigation component to manage fragment operations.
* Reactive UIs using LiveData observables and Data Binding.
* It consists of one fragment which is fully tested by Espresso.

<p align="center">
<img src="https://user-images.githubusercontent.com/33812602/96847522-946dd880-1453-11eb-869e-14ad576e2391.jpg" width="250" />
</p>

### Branches
|     Sample     | Description |
| ------------- | ------------- |
| [master](https://github.com/Mustafashahoud/Tandem/tree/master) | The base for the rest of the other branches. <br/>Uses Kotlin, Architecture Components, Coroutines + Flow, Dagger, Retrofit Data Binding, etc. |
| [room-cache](https://github.com/Mustafashahoud/Tandem/tree/room-cache)| Same like master branch but it uses Room db for caching data implementing single source of truth|
| [paging3-network-db-livedata](https://github.com/Mustafashahoud/Tandem/tree/paging3-network-db-livedata)| Added Paging3 library, It uses RemoteMediator with Room DAO + PagingSource as single source of truth, exposes data as LiveData|
| [paging3-network-db-flow](https://github.com/Mustafashahoud/Tandem/tree/paging3-network-db-flow)| Added Paging3 library, It uses RemoteMediator with Room DAO + PagingSource as single source of truth, exposes data as Flow|

## Paging 3
* The interesting thing  about this repository is that it implements the [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) Library that has so many features that simplified complicated process creating RecyclerView with paging.
    - Loading small chunk of data that reduces usage of network bandwidth and system resources.
    - Built-in support for error handling, including refresh and retry capabilities.
    - Built-in separator, header, and footer support.
    - Automatically requests the correct page when the user has scrolled to the end of the list.
    - Ensures that multiple requests are not triggered at the same time.

## Libraries
- 100% Kotlin
- MVVM Architecture
- Architecture Components (Lifecycle, LiveData, ViewModel, Paging, Navigation Component, DataBinding)
- [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) For handling concurrent tasks
- [Dagger2](https://github.com/google/dagger) for dependency injection
- [Retrofit2](https://github.com/square/retrofit) for REST API
- [Glide](https://github.com/bumptech/glide) for loading images
- [Mockito-kotlin](https://github.com/nhaarman/mockito-kotlin) for Junit mock test
- [Espresso](https://developer.android.com/training/testing/espresso) for UI testing

## MVVM (Model – View – ViewModel)
is an architectural pattern in programming. The main task of the pattern is to separate presentation logic from business logic. The most important component in MVVM is the ViewModel, which behaves more like a model and less like a view. It is responsible for converting and delegating data objects to presentation logic that displays those objects to the user on the device screen.

![MVVM](https://user-images.githubusercontent.com/33812602/96825222-e81afa80-1430-11eb-8cc2-1025bb568ef3.PNG)

## MVVM Best Practice:
* Avoid references to Views in ViewModels.
The ViewModel should never have any view or UI controllers (activities and fragments) references because ViewModels have different scopes than activities/fragments. While a ViewModel is alive and running, an activity can be in any of its lifecycle states. Activities/fragments can be destroyed and created again while the ViewModel is unaware

* Instead of pushing data to the UI, let the UI observe changes  (For example using LiveData)
Fragments/Activities are intended to display UI data, react to user actions, or handle operating system communication, they shouldn't know how the data is coming they only observe it, ViewModel prepares data for the UI. ViewModel is retained during configuration changes, like rotating the phone.

* Add a data repository as the single-point entry to your data.
Each component in MVVM depends only on the component one level below it. For example, activities and fragments depend only on a view model. The repository is the only class that depends on multiple other classes; For example, the repository depends on a persistent data model and a remote backend data source. In my case I am using only remote backend data source.

* Expose information about the state of your data using a wrapper or another LiveData.
In my app I have used Wrapper named Resource that can expose three States: LOADING, SUCCESS, ERROR.


## Implementation
*  ### Fetching Community data from the remote API
I have used Retrofit which is a REST Client for Java and Android that makes it easy to retrieve and upload JSON (or other structured data) via a REST based webservice.
 * As it is known we can configure which converter is used for the data serialization by specifying the factory.
In my case I have used: GsonConverterFactory. (MoshiConverterFactory can also be used)
We can also create our own CallAdapter that converts the Call into a ApiResponse it is Common class used by API responses representing three types of:
- ApiSuccessResponse represents the Success response.
- ApiFailureResponse represent tow type of errors: Error response (e.g. server error), Exception Response (e.g. network connection error)
Then I have created a factory of that CallAdapter to be used when creating the Retrofit service.
```
@GET("/api/community_{page}.json")
 suspend fun getCommunityMembers(@Path("page") page: Int): ApiResponse<CommunityResponse>
```
There is a good article about CallAdapter in [here]( https://proandroiddev.com/create-retrofit-calladapter-for-coroutines-to-handle-response-as-states-c102440de37a)

*  ### Dependency Injection:
Dependency injection is a technique in which an object receives other objects that it depends on. These other objects are called dependencies. In the typical "using" relationship the receiving object is called a client and the passed (that is, "injected") object is called a service.
* #### Dependency injection advantages:
- Dependency injection provides the flexibility of configurable.
- Dependency injection makes it much easier to unit test in isolation using stubs or mock objects that simulate other objects not under test.
- Dependency injection allows a client to remove all knowledge of a concrete implementation that it needs to use. This helps isolate the client from the impact of design changes and defects. It promotes reusability, testability and maintainability
- Dependency Injection decreases coupling between a class and its dependency

- In this app I have used [Dagger2](https://github.com/google/dagger) for dependency injection
Dagger is a fully static, compile-time dependency injection framework for Java, Kotlin, and Android. It is an adaptation of an earlier version created by Square and now maintained by Google.
I have created a AppInjector that has a method ```fun init(tandemApp: TandemApp)``` where tandemApp is an instance of the Application passed during runtime. This Method is called in onCreate of TandemApp as follows:
```
class TandemApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        AppInjector.init(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}

```
- Note: I could have used Dagger Hilt which is much easier than Dagger2 and it basically gets rid of a lot of boilerplate code.
But the only problem with it, as u can see from [Hitl Doc]( https://dagger.dev/hilt/testing) is that Hilt does not currently support FragmentScenario for testing fragments, however there is still a workaround.


 * ### Coroutines
-----------------------------
 * #### Coroutines VS RXJava
They're different tools with different strengths. Like a tank and a cannon, they have a lot of overlap but are more or less desirable under different circumstances.
   - Coroutines Are light weight threads for asynchronous programming.
   - RX-Kotlin/RX-Java is functional reactive programming, its core pattern relay on observer design pattern, so you can use it to handle user interaction with UI while you still using coroutines as the main core for background work.
   - Creating a coroutine is a lot cheaper than creating a thread.


* #### How do I choose Coroutines or RX-Kotlin to do some behaviour?
-----------------------------
   - Coroutines: When we have concurrent tasks like you would fetch data from Remote service, database, any background processes, sure you can use RX in such cases too, but it looks like you use a tank to kill an ant.
   - RX-Kotlin: When you would to handle a stream of UI actions like user scrolling, clicks, update UI upon some events ... etc.

 * #### What are the Coroutines benefits?
-----------------------------

   - Writing asynchronous code is a sequential manner.
   - Costing of creating coroutines is much cheaper to create threads.
   - Don't be over-engineered to use observable pattern, when no need to use it.
   - parent coroutine can automatically manage the life cycle of its child coroutines for you.


* ### Repository
--------------------------------
Repository modules are responsible for handle data operations. By ensuring this, Repository modules can provide a clean API to the rest of the app and simplify the job of the consumer ViewModel. Repository modules should know where to get the data from and what API calls to make when data is updated if necessary.
They can be considered as mediators between different data sources (REST services, Databases ..)

* I have used [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) in my app, A flow is an asynchronous version of a Sequence, a flow produces each value on-demand whenever the value is needed.
- Flow includes full support for coroutines. That means you can build, transform, and consume a Flow using coroutines.
- You can also control concurrency, which means coordinating the execution of several coroutines declaratively with Flow.
- Flow can be used in a fully-reactive programming style. like RxJava.
- Flow also supports suspending functions on most operators. By using suspending operations inside of a flow, it often results in shorter and easier to read code than the equivalent code in a fully-reactive style.

- My repository looks like:
```
@OpenForTesting
class CommunityRepository @Inject constructor(
    private val service: TandemService,
    private val dispatcherIO: CoroutineDispatcher
) {

    @WorkerThread
    suspend fun getCommunityMembers(
        page: Int
    ): Flow<Resource<List<Member>>> {
        return flow {
            emit(Resource.loading(null))
            service.getCommunityMembers(page).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Resource.success(it.members, it.members.size != 20))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(Resource.error(message(), null))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                emit(Resource.error(message(), null))
            }

        }.flowOn(dispatcherIO)

    }
}
```
- onSuccessSuspend, onErrorSuspend, onExceptionSuspend are nothing but suspend extension functions for ApiResponse. something like:
```
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onSuccessSuspend(
    onResult: suspend ApiResponse.ApiSuccessResponse<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiSuccessResponse) {
        onResult(this)
    }
    return this
}
```
* As we can notice I am specifying the Dispatcher by ```flowOn(dispatcherIO)``` which is the Dispatcher that this Flow is going to work on and it is basically Dispatchers.IO but it is injected (Provided by Dagger) for better testability (I will talk about it later).

* Also  ```service: TandemService``` is being injected for better testability, so that when we test ```CommunityRepository``` we just need mock ```service``` and expect values to ensure we are testing in separation.
* For success responses we emit ```Resource.success(it.members, it.members.size != 20))```
* For error responses we emit ```(Resource.error(message(), null))```
* As you can notice I have handled the last page condition in the resource by adding the statement ``` it.members.size != 20```
So for each request we have if it is a success we check if we are on the last page and in the UI when observing the data, we can react accordingly. (That could also have been done in the ApiResponse)

* ### ViewModel
-------------------------

The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way. The ViewModel class allows data to survive configuration changes such as screen rotations.

* My viewModel looks like:
```
@OpenForTesting
class CommunityViewModel @Inject constructor(
    private val repository: CommunityRepository,
    private val dispatcherIO: CoroutineDispatcher
) :
    ViewModel() {

    private val pageLiveData: MutableLiveData<Int> = MutableLiveData()

    private var pageNumber = 1

    init {
        pageLiveData.postValue(1)
    }

    val membersListLiveData = pageLiveData.switchMap { pageNumber ->
        liveData(viewModelScope.coroutineContext + dispatcherIO) {
            val members = repository.getCommunityMembers(pageNumber).asLiveData()
            emitSource(members)
        }
    }

    fun loadMore() {
        pageNumber++
        pageLiveData.postValue(pageNumber)
    }

    fun refresh() {
        pageLiveData.value?.let {
            pageLiveData.value = it
        }
    }
}
```

* ```repository: CommunityRepository``` and ```dispatcherIO: CoroutineDispatcher``` are injected and provided by Dagger for better testability.
* The ViewModel exposes ```membersListLiveData``` that is going to be observed by the UI My fragment and changing the UI accordingly with the Livedata changes (Recommended to exposes only LiveData not MutableLiveData (it should be used internally))
* The way it works is when changing the value of ```pageLiveData```, ```repository.getCommunityMembers(pageNumber)``` will be called returning Flow that is going to be converted to LiveData.
* The Most important thing here is that I am using ```viewModelScope``` which is using to launch the coroutines, and it will destroy it when the ViewModel is destroyed (onCleared() is called) with having to destroy it ourselves (Not like RX-Java and disposables)

* ### Fragment
------------------------

* In the fragment I am using Databinding to bind LOADING Resource to the progressbar visibility, ERROR Resource to a snackbar showing the error message and the data of each member in the community using BindingAdapter. The  ```onViewCreated``` in my fragment looks like:

```
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        with(binding) {
            lifecycleOwner = this@CommunityFragment
            vm = viewModel
            callback = object : RetryCallback {
                override fun retry() {
                    viewModel.refresh()
                }
            }
        }

        initRecyclerView()
        subscribeUi()

    }
```
* I am observing the liveData as follows:

```
    private fun subscribeUi() {
        viewModel.membersListLiveData.observe(viewLifecycleOwner, { result ->
            if (result.status == Status.SUCCESS && !result.data.isNullOrEmpty()) {
                adapter.submitList(result.data)
            }
        })
    }

```
* It is very important to pass ```viewLifecycleOwner``` as the LifeCycleOwner because when fragment gets detached it won't be destroyed but the ```onDestroyView``` gets called so here the Fragment survives and in case passing the fragment is LifeCycleOwner the observer will not be destroyed and we would have ```onChanged``` called twice.


* ## Junit Test
-----------------------------
* As it is know all Kotlin classes are final that means we cannot mock them or stub them the way I’ve handled this is by using the same mechanism that was used in [GithubBrowserSample]( https://github.com/android/architecture-components-samples/tree/main/GithubBrowserSample)
The idea is that making kotlin classes open in our test build but also keep them final in our releases builds, then we can mock in our tests without having to make classes always open, that can be done with the Kotlin compiler plugin kotlin-allopen. What we need to do is creating two annotations one of them is put in src/debug which has OpenClass and the other one is put in src/release but without OpenClass:
```
/**
	 * This annotation allows us to open some classes for mocking purposes while they are final in
	 * release builds.
	 */
	@Target(AnnotationTarget.ANNOTATION_CLASS)
	annotation class OpenClass


	/**
	 * Annotate a class with [OpenForTesting] if you want it to be extendable in debug builds.
	 */
	@OpenClass
	@Target(AnnotationTarget.CLASS)
	annotation class OpenForTesting

```

```
@Target(AnnotationTarget.CLASS)
	annotation class OpenForTesting

```
Then Add the classpath to the ``` classpath "org.jetbrains.kotlin:kotlin-allopen:$kotlin_version"``` root build.gradle file.
Then in the app build.gradle apply the plugin and specify the annotation class.
```
apply plugin: 'kotlin-allopen'

allOpen {
    // allows mocking for classes w/o directly opening them for release builds
    annotation 'com.mustafa.tandem.testing.OpenClass'
}
```
Finally, I just need to add @OpenForTesting for the classes I need to be open in Debug build.

* ### Coroutines Testing
* When testing with Coroutines the Dispatchers.Main requires main Looper which is not available in the unit tests. So the solution is to override Dispatchers.Main with TestCoroutineDispatcher, That is why we always need to write a general rule to use TestCoroutineDispatcher, We simply need to set the main dispatcher to TestCoroutineDispatcher whenever a test is started.
* TestCoroutineDispatcher has runBlockingTest which will immediately progress delays with a virtual clock. It can be used for faster tests instead of runBlocking.
* when you start a new coroutine with other dispatchers, that coroutine will be executed in a different thread This is why we need to inject the dispatchers.
* Here is an example of a Junit test for ```CommunityRepository```:

```
    @Test
    fun getCommunityMembers_onlyOneMember() = coroutinesRule.testDispatcher.runBlockingTest {

        val mockMember = Member("Mustafa", listOf("SP", "HU"), listOf("EN", "DE"), "URL", 1, "TOPIC")
        val members = listOf(mockMember)
        val mockResponse = CommunityResponse(null, members, "success")
        val mockData = mockResponse.members

        val call = successCall(mockResponse)
        whenever(service.getCommunityMembers(1)).thenReturn(call)

        repository.getCommunityMembers(1).collectIndexed { index, resource ->
            if (index == 0) assertThat(resource.status, `is`(Status.LOADING))
            if (index == 1) {
                assertThat(resource.status, `is`(Status.SUCCESS))
                resource.data?.let { members ->
                    assertThat(members[0].firstName, `is`("Mustafa"))
                    assertThat(members[0].topic, `is`("TOPIC"))
                    assertThat(members[0].referenceCnt, `is`(1))
                    assertThat(members[0].learns[0], `is`("SP"))
                    assertThat(members[0].natives[0], `is`("EN"))
                    assertThat(members[0].pictureUrl, `is`("URL"))
                    assertThat(members, `is`(mockData))
                }
                assertThat(resource.message, `is`(nullValue()))
                // Since we have only one members, when the count of the response members is less than 20, that means we are in the last page
                assertThat(resource.isLastPage, `is`(true))
            }

        }
        verify(service, times(1)).getCommunityMembers(1)
        verifyNoMoreInteractions(service)

    }

```

* After mocking the service and expecting it to return a fake response we test our  ```getCommunityMembers``` with page and that can be done in different ways as Flow as really cool operators, one of them is ```collectIndexed``` which provides the index and the data not having to use delay function. So in the above method, I am asserting that the first resource is Loading and then the second resource is the data that was fetched successfully.

* For testing the viewModel we should mock the repository, and then expecting the repository to return fake Flows, then observing the Livedata on a mock observer, changing the liveData value, and finally asserting for example:

```
 @Test
    fun getCommunityMembersSuccessTest() = coroutinesRule.testDispatcher.runBlockingTest {
        val observer = mock<Observer<Resource<List<Member>>>>()

        val members = MockTestUtil.createMembers(20)
        val resourceSuccess = Resource.success(members, false)

        val flow = flow {
            emit(resourceSuccess)
        }
        whenever(repository.getCommunityMembers(anyInt())).thenReturn(flow)

        viewModel.membersListLiveData.observeForever(observer)

        // I am changing pageLiveData with pageLiveData.postValue(1) in init block
        verify(repository).getCommunityMembers(1)

        verify(observer).onChanged(resourceSuccess)

        assertThat(viewModel.membersListLiveData.value?.data?.size, `is`(members.size))
        assertThat(
            viewModel.membersListLiveData.value?.data?.get(0)?.firstName, `is`(members[0].firstName)
        )
        assertThat(viewModel.membersListLiveData.value?.data, `is`(members))

        // Or resourceSuccess.status
        assertThat(viewModel.membersListLiveData.value?.status, `is`(Status.SUCCESS))
        assertThat(viewModel.membersListLiveData.value, `is`(resourceSuccess))

        viewModel.membersListLiveData.removeObserver(observer)
    }
```

* ## UI Test
--------------------------
* One of the main reasons why I have chosen to use Navigation component is the ease of testing as we don't have to create TestActivity and then attach the fragment to it.
* We only need to use FragmentScenario so the init method in CommunityFragmentTest looks like:

```
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
```
* Then for testing the UI we just need to post valus to he liveData and assert the views for example:

```
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

```

```
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
```


* For UI testing when using DataBinding is important to use an espresso idling resource implementation that reports idle status for all data binding layouts since Data Binding uses a mechanism to post messages which Espresso doesn't track yet.

* ## Alternative Solutions:

* This App could also be done by using RX-Java by using Observable<Type> as the return type in the API service and then using RxJava2CallAdapter ```.addCallAdapterFactory(RxJava2CallAdapterFactory.create())``` when creating Retrofit instance. the method in the ViewModel would look similar to the following:

```
fun getCommunityMembers(page: Int) {
        subscription = repository.getCommunityMembers(page)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doOnSubscribe {
                membersLiveDate.value = Resource.loading(null)
            }
            .subscribe(
                { result ->
                    membersLiveDate.value = Resource.success(result.members, result.members.size != 20))
                },
                {
                    membersLiveDate.value = Resource.error(it.localizedMessage, null)
                }
            )
    }
```
Then we will have to clear the subscription as follows:

```
 override fun onCleared() {
        super.onCleared()
        if (!subscription.isDisposed) {
            subscription.dispose()
        }
    }
```

* It can also be done by Using coroutines and liveData in the Repository without using Flow something similar to:

```
suspend fun getCommunityMembers(page: Int): LiveData<List<Member>> {
        val membersLiveDate = MutableLiveData<Resource<List<Member>>>()
        val members: LiveData<Resource<List<Member>>> = membersLiveDate
        withContext(Dispatchers.IO) {
            val response = tandemService.getCommunityMembers(page).apply { response ->
                response.onSuccess {
                    data?.let { result ->
                        mutableLiveDate.postValue(Resource.success(result.members, result.members.size != 20))
                    }
                }.onError {
                    mutableLiveDate.postValue(Resource.error(message(), null))
                }.onException {
                    mutableLiveDate.postValue(Resource.error(message(), null))
                }
            }
        }
        return members
    }
```
Then in the ViewModel:

```
val membersLiveData = pageLiveData.switchMap { pageNumber ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val members = repository.getCommunityMembers(pageNumber)
            emitSource(members)
        }
    }
```

* ## Proposed Improvements:

- If I had to create a real production App, I would have cashed the data using Room following "single source of truth pattern".
- Using the Paging Library, The idea is when building production apps we would want to query the whole data (old data and new queried data) each time
- For that, we should use ListAdapter with diffUtil (better performance), but the user usually interacts with only a small chunk of data at a time.
- The Paging Library helps you load and display small chunks of data at a time. Loading partial data on-demand reduces the usage of network bandwidth and system resources.

