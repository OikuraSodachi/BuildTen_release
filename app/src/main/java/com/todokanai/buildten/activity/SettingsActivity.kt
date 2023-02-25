package com.todokanai.buildten.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.todokanai.buildten.databinding.ActivitySettingsBinding
import com.todokanai.buildten.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private val binding by lazy {ActivitySettingsBinding.inflate(layoutInflater)}
    private val viewModel : SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.mPath.asLiveData().observe(this){
            viewModel.pathList = it.map{it.scanPath!!}
        }

        val intentmain = Intent(this,MainActivity::class.java)
        binding.Backbtn.setOnClickListener {startActivity(intentmain)}

        binding.scanBtn.setOnClickListener { viewModel.scanBtn() }
        binding.confirmBtn.setOnClickListener {
            viewModel.confirmBtn(binding.inputText.text)
            binding.inputText.setText("")
        }

    }
}