package com.todokanai.buildten.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.todokanai.buildten.adapter.DirectoryRecyclerAdapter
import com.todokanai.buildten.databinding.FragmentDirectoryBinding
import com.todokanai.buildten.viewmodel.DirectoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DirectoryFragment : Fragment() {

    private val binding by lazy { FragmentDirectoryBinding.inflate(layoutInflater) }
    private val viewModel : DirectoryViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val adapter = DirectoryRecyclerAdapter{
            viewModel.onItemClick(it)

        }
        binding.directoryRecyclerView.adapter = adapter
        binding.directoryRecyclerView.layoutManager = LinearLayoutManager(context)


        viewModel.pathListFlow.asLiveData().observe(viewLifecycleOwner){
            adapter.mDirectoryList = it
            adapter.notifyDataSetChanged()
        }


        return binding.root
    }

}