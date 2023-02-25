package com.todokanai.buildten.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.todokanai.buildten.adapter.TrackRecyclerAdapter
import com.todokanai.buildten.databinding.FragmentTrackBinding
import com.todokanai.buildten.viewmodel.TrackViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackFragment : Fragment() {

    private val binding by lazy{ FragmentTrackBinding.inflate(layoutInflater) }
    private val viewModel : TrackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val adapter = TrackRecyclerAdapter{
            viewModel.onItemClick(requireActivity(), it)
            // viewModel에 연결될 내용 작성
        }
        binding.trackRecyclerView.adapter = adapter
        binding.trackRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.swipe.setOnRefreshListener {

            adapter.notifyDataSetChanged()
            binding.swipe.isRefreshing = false
        }



        viewModel.trackListFlow.asLiveData().observe(viewLifecycleOwner){
            adapter.trackList = it
            adapter.notifyDataSetChanged()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

}