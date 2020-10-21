package com.mustafa.tandem

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.coroutines.ContinuationInterceptor


@ExperimentalCoroutinesApi
class MainCoroutinesRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope()  {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
//
//@ExperimentalCoroutinesApi
//class MainCoroutinesRule : TestWatcher(), TestCoroutineScope by TestCoroutineScope() {
//
//
//    override fun starting(description: Description?) {
//        super.starting(description)
//        Dispatchers.setMain(this.coroutineContext[ContinuationInterceptor] as CoroutineDispatcher)
//    }
//
//    override fun finished(description: Description?) {
//        super.finished(description)
//        Dispatchers.resetMain()
//    }
//}


/**
 * @ExperimentalCoroutinesApi
class MainCoroutinesRule : TestRule, TestCoroutineScope by TestCoroutineScope() {

private val testCoroutinesDispatcher = TestCoroutineDispatcher()
private val testCoroutineScope = TestCoroutineScope(testCoroutinesDispatcher)

override fun apply(base: Statement?, description: Description?) = object : Statement() {
override fun evaluate() {
Dispatchers.setMain(testCoroutinesDispatcher)
base?.evaluate()
Dispatchers.resetMain()
testCoroutineScope.cleanupTestCoroutines()
}
}

fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) {
testCoroutineScope.runBlockingTest { block() }
}
}
 */
