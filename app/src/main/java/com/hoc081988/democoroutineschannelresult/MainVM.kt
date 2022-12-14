package com.hoc081988.democoroutineschannelresult

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface MainSingleEvent<out T : MainSingleEvent<T>> {
  interface Key<out T : MainSingleEvent<T>>

  val key: Key<T>

  data class HomeFragmentResult(val text: String) : MainSingleEvent<HomeFragmentResult> {
    override val key = HomeFragmentResult

    companion object : Key<HomeFragmentResult>
  }

  data class DashboardFragmentResult(val text: String) : MainSingleEvent<DashboardFragmentResult> {
    override val key = DashboardFragmentResult

    companion object : Key<DashboardFragmentResult>
  }

  data class HomeDetailsResult(val text: String) : MainSingleEvent<HomeDetailsResult> {
    override val key = HomeDetailsResult

    companion object : Key<HomeDetailsResult>
  }

  companion object {
    val KEYS: Set<Key<SomeMainSingleEvent>> = setOf(
      HomeFragmentResult,
      DashboardFragmentResult,
      HomeDetailsResult,
    )
  }
}

private typealias SomeMainSingleEvent = MainSingleEvent<*>

class MainVM : ViewModel() {
  private val eventChannels: Map<MainSingleEvent.Key<SomeMainSingleEvent>, Channel<SomeMainSingleEvent>> =
    MainSingleEvent.KEYS.associateBy(
      keySelector = { it },
      valueTransform = { Channel<MainSingleEvent<SomeMainSingleEvent>>(Channel.UNLIMITED) }
    )

  fun <T : MainSingleEvent<T>> sendEvent(event: T) {
    checkNotNull(eventChannels[event.key]) { "Must register ${event.key} in MainSingleEvent.Companion.KEYS before using!" }
      .trySend(event)
      .getOrThrow()
      .also { Log.d("@@@", "Sent $event") }
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : MainSingleEvent<T>, K : MainSingleEvent.Key<T>> receiveEventFlow(key: K): Flow<T> =
    checkNotNull(eventChannels[key]) { "Must register $key in MainSingleEvent.Companion.KEYS before using!" }
      .receiveAsFlow()
      .map { it as T }

  override fun onCleared() {
    super.onCleared()
    eventChannels.values.forEach { it.close() }
  }
}