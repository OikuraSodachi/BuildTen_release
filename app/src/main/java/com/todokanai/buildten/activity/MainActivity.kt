package com.todokanai.buildten.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.todokanai.buildten.adapter.FragmentAdapter
import com.todokanai.buildten.databinding.ActivityMainBinding
import com.todokanai.buildten.fragment.PlayingFragment
import com.todokanai.buildten.fragment.TrackFragment
import com.todokanai.buildten.service.ForegroundPlayService
import com.todokanai.buildten.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private val viewModel : MainViewModel by viewModels()
    lateinit var activityResult: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.trackPager.isUserInputEnabled = false
        activityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted)
                    finish()
            }
        activityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


        val intentService = Intent(this, ForegroundPlayService::class.java)
        viewModel.launchForeground(this,intentService)
      //  ContextCompat.startForegroundService(applicationContext, intentService)    //----- 서비스 개시


        binding.Exitbtn.setOnClickListener { viewModel.exit(this,intentService)}      //----Exitbtn에 대한 동작

        val intentSetting = Intent(this, SettingsActivity::class.java)
        binding.Settingsbtn.setOnClickListener { startActivity(intentSetting) }     //Settingsbtn에 대한 동작

        //---------탭 넘기기 관련 코드
        val fragmentList = listOf(TrackFragment(), PlayingFragment())
        val adapter = FragmentAdapter(this)
        adapter.fragmentList = fragmentList
        binding.trackPager.adapter = adapter
        val tabTitles = listOf("Music", "Playing")
        TabLayoutMediator(binding.tabLayout, binding.trackPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }
}