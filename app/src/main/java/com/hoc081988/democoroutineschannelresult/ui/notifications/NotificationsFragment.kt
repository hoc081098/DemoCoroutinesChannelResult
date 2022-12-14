package com.hoc081988.democoroutineschannelresult.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.hoc081988.democoroutineschannelresult.MainSingleEvent
import com.hoc081988.democoroutineschannelresult.MainVM
import com.hoc081988.democoroutineschannelresult.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

  private var _binding: FragmentNotificationsBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  private val mainVM by viewModels<MainVM>(
    ownerProducer = { requireActivity() }
  )
  private val vm by viewModels<NotificationsViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    mainVM
      .receiveEventFlow(MainSingleEvent.HomeFragmentResult)
      .onEach(vm::handleHomeFragmentResult)
      .launchIn(lifecycleScope)

    mainVM
      .receiveEventFlow(MainSingleEvent.DashboardFragmentResult)
      .onEach(vm::handleDashboardFragmentResult)
      .launchIn(lifecycleScope)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val homeResultsAdapter = TextResultAdapter()
    binding.recyclerHomeResults.run {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(requireContext())
      adapter = homeResultsAdapter
      addItemDecoration(MaterialDividerItemDecoration(context, RecyclerView.VERTICAL))
    }

    val dashboardResultsAdapter = TextResultAdapter()
    binding.recyclerDashboardResults.run {
      setHasFixedSize(true)
      layoutManager = LinearLayoutManager(requireContext())
      adapter = dashboardResultsAdapter
      addItemDecoration(MaterialDividerItemDecoration(context, RecyclerView.VERTICAL))
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(STARTED) {
        vm.homeResults
          .collect(homeResultsAdapter::submitList)
      }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(STARTED) {
        vm.dashboardResults
          .collect(dashboardResultsAdapter::submitList)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}