package com.hoc081988.democoroutineschannelresult.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hoc081988.democoroutineschannelresult.MainSingleEvent
import com.hoc081988.democoroutineschannelresult.MainVM
import com.hoc081988.democoroutineschannelresult.R
import com.hoc081988.democoroutineschannelresult.databinding.FragmentHomeDetailsBinding

class HomeDetailsFragment : Fragment(R.layout.fragment_home_details) {

  private var _binding: FragmentHomeDetailsBinding? = null

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
    _binding = FragmentHomeDetailsBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.button.setOnClickListener {
      mainVM.sendEvent(
        MainSingleEvent.HomeDetailsResult(
          text = binding.textInputLayout
            .editText!!
            .text
            ?.toString()
            .orEmpty(),
        )
      )

      findNavController().popBackStack()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}