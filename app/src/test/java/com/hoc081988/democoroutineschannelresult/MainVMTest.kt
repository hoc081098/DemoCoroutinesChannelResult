package com.hoc081988.democoroutineschannelresult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainVMTest {
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun bufferEvents() = runTest(testDispatcher) {
    val vm = MainVM()

    val originalEvents = (0..50).map {
      val text = it.toString()

      when (it % 3) {
        0 -> MainSingleEvent.HomeFragmentResult(text)
        1 -> MainSingleEvent.DashboardFragmentResult(text)
        else -> MainSingleEvent.HomeDetailsResult(text)
      }
    }
    originalEvents.forEach(vm::sendEvent)
    delay(100)

    val actualEvents = MainSingleEvent.KEYS.map { vm.receiveEventFlow(it) }
      .merge()
      .take(originalEvents.size)
      .toList()

    Assert.assertEquals(
      originalEvents.sortedBy { it.hashCode() },
      actualEvents.sortedBy { it.hashCode() }
    )
  }

  @Test
  fun throwsAfterCleared() = runTest(testDispatcher) {
    val vm = MainVM()
    vm.onCleared()

    Assert.assertThrows(Exception::class.java) {
      vm.sendEvent(MainSingleEvent.HomeFragmentResult("1"))
    }

    vm.receiveEventFlow(MainSingleEvent.HomeFragmentResult)
      .toList()
      .let { Assert.assertTrue(it.isEmpty()) }
  }
}