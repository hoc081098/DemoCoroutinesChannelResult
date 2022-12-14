package com.hoc081988.democoroutineschannelresult.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoc081988.democoroutineschannelresult.MainSingleEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class NotificationsViewModel : ViewModel() {
  private val _homeFragmentResultEvent = Channel<String>(Channel.UNLIMITED)
  private val _dashboardFragmentResultEvent = Channel<String>(Channel.UNLIMITED)

  val homeResults = _homeFragmentResultEvent
    .consumeAsFlow()
    .scan(emptyList<String>()) { acc, value -> acc + value }
    .stateIn(
      viewModelScope,
      SharingStarted.Lazily,
      emptyList()
    )

  val dashboardResults = _dashboardFragmentResultEvent
    .consumeAsFlow()
    .scan(emptyList<String>()) { acc, value -> acc + value }
    .stateIn(
      viewModelScope,
      SharingStarted.Lazily,
      emptyList()
    )

  fun handleHomeFragmentResult(event: MainSingleEvent.HomeFragmentResult) =
    _homeFragmentResultEvent.trySend(event.text).getOrThrow()

  fun handleDashboardFragmentResult(event: MainSingleEvent.DashboardFragmentResult) =
    _dashboardFragmentResultEvent.trySend(event.text).getOrThrow()
}