package com.todokanai.buildten.tools

import com.todokanai.buildten.R

class IconSetter {

    fun setLoopingImage(isLooping:Boolean?) : Int {
        if(isLooping==true) {
            return R.drawable.ic_baseline_repeat_one_24
        } else {
            return R.drawable.ic_baseline_repeat_24
        }
    }

    fun setPausePlayImage(isPlaying:Boolean?) : Int {
        if(isPlaying==true) {
            return R.drawable.ic_baseline_pause_24
        } else {
            return R.drawable.ic_baseline_play_arrow_24
        }
    }

    fun setShuffleImage(isShuffled:Boolean?) : Int {
        if(isShuffled==true){
            return R.drawable.ic_baseline_shuffle_24
        } else {
            return R.drawable.ic_baseline_arrow_right_alt_24
        }
    }

}