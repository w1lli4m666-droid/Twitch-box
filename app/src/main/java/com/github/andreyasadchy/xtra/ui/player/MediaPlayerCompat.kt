package com.github.andreyasadchy.xtra.ui.player

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build

internal fun MediaPlayer.setPlaybackSpeedCompat(speed: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        playbackParams = PlaybackParams().apply { this.speed = speed }
    }
}

internal fun MediaPlayer.getPlaybackSpeedCompat(): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) playbackParams.speed else 1f
}
