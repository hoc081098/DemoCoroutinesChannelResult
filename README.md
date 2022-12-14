# DemoCoroutinesChannelResult

Use `Kotlinx Coroutines Channel` to send and receive events between Fragments.

## Author: [Petrus Nguyễn Thái Học](https://github.com/hoc081098)

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fhoc081098%2FDemoCoroutinesChannelResult&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

<p align="center">
    <img src="demo.gif" height="480"/>
</p>

## Overview

### 1. Create `MainSingleEvent` class.

```kotlin
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
```

### 2. Create `MainVM` class with buffered channels as event bus.

```kotlin
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
}
```

### 3. Send and receive events in `Fragment`s.

We will share `MainVM` instance between `Fragment`s using `Activity` as owner.

```kotlin
private val mainVM by viewModels<MainVM>(
  ownerProducer = { requireActivity() }
)

// send in HomeFragment
mainVM.sendEvent(MainSingleEvent.HomeFragmentResult("Hello from HomeFragment"))

// receive in others
mainVM.receiveEventFlow(MainSingleEvent.HomeFragmentResult)
  .onEach { Log.d("###", "Received $it") }
  .launchIn(lifecycleScope)
```