package com.todokanai.buildten.fragment

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.todokanai.buildten.databinding.FragmentPlayingBinding
import com.todokanai.buildten.myobjects.MyObjects
import com.todokanai.buildten.myobjects.MyObjects.currentTrackValue
import com.todokanai.buildten.viewmodel.PlayingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayingFragment : Fragment() {

    private val binding by lazy { FragmentPlayingBinding.inflate(layoutInflater) }
    private val viewModel: PlayingViewModel by viewModels()
    private val mediaPlayer = MyObjects.mMediaPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        viewModel.currentTrack.observe(viewLifecycleOwner) {
            it?.let {
                binding.playerImage.setImageURI(viewModel.getAlbumArt(it))
                binding.artist.text = viewModel.getArtistName(it)
                binding.songTotalTime.text = viewModel.getTotalTime(it)
                binding.title.text = viewModel.getTitle(it)
            }
        }


        binding.repeatButton.setOnClickListener { viewModel.repeatButton(requireActivity()) }
        binding.previousButton.setOnClickListener { viewModel.previousButton(requireActivity()) }
        binding.playPauseButton.setOnClickListener { viewModel.playPauseButton(requireActivity()) }
        binding.nextButton.setOnClickListener { viewModel.nextButton(requireActivity()) }
        binding.shuffleButton.setOnClickListener { viewModel.shuffleButton(requireActivity()) }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        fun seekBarSet() {
            if (currentTrackValue!=null/* mediaPlayer가 비어있지 않다면*/) {
                lifecycleScope.launch {
                    while (mediaPlayer.isPlaying) {
                        binding.seekBar.progress = viewModel.getCurrentPosition(mediaPlayer)
                        binding.songCurrentProgress.text =
                            SimpleDateFormat("mm:ss").format(binding.seekBar.progress)
                        delay(1)
                    }
                }

                binding.seekBar.max = viewModel.getSeekbarMax(mediaPlayer)
                val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) {
                            //sets the playing file progress to the same seekbar progressive, in relative scale
                            mediaPlayer.seekTo(progress)

                            //Also updates the textView because the coroutine only runs every 1 second
                            binding.songCurrentProgress.text = viewModel.getCurrentProgress(mediaPlayer)
                        } else {

                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                }
                binding.seekBar.setOnSeekBarChangeListener(seekBarListener)
                //  binding.songTotalTime.text = viewModel.getTotalTime()
            }
        }
        seekBarSet()
        //--------------
        viewModel.getCurrentPosition(mediaPlayer)




        //----------


        viewModel.isPlayingState.observe(viewLifecycleOwner){
            seekBarSet()
            binding.playPauseButton.setImageResource(viewModel.setPausePlayImage(it))
        }
        viewModel.isLoopingState.observe(viewLifecycleOwner){
                binding.repeatButton.setImageResource(viewModel.setLoopingImage(it))
        }
        viewModel.isShuffledState.observe(viewLifecycleOwner){
            binding.shuffleButton.setImageResource(viewModel.setShuffleImage(it))
        }



        viewModel.currentTrack.observe(viewLifecycleOwner) {

            it?.let {
                binding.playerImage.setImageURI(viewModel.getAlbumArt(it))
                binding.artist.text = viewModel.getArtistName(it)
                binding.songTotalTime.text = viewModel.getTotalTime(it)
                binding.title.text = viewModel.getTitle(it)
            }
        }
    }

}