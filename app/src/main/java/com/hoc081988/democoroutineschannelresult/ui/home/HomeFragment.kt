package com.hoc081988.democoroutineschannelresult.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hoc081988.democoroutineschannelresult.MainSingleEvent
import com.hoc081988.democoroutineschannelresult.MainVM
import com.hoc081988.democoroutineschannelresult.R
import com.hoc081988.democoroutineschannelresult.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

  private var _binding: FragmentHomeBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  private val mainVM by viewModels<MainVM>(
    ownerProducer = { requireActivity() }
  )

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
      ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textHome
    homeViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.button.setOnClickListener {
      val text = binding.textInputLayout
        .editText!!
        .text
        ?.toString()
        ?.takeIf { it.isNotBlank() }
        ?: return@setOnClickListener

      mainVM.sendEvent(
        MainSingleEvent.HomeFragmentResult(
          text = text,
        )
      )

      binding.textInputLayout.editText!!.setText("")
    }

    binding.button2.setOnClickListener {
      kotlin.runCatching { findNavController().navigate(R.id.action_navigation_home_to_homeDetailsFragment) }
    }

    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(STARTED) {
        mainVM
          .receiveEventFlow(MainSingleEvent.HomeDetailsResult)
          .collect { binding.textHome.text = "Details result: ${it.text}" }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}