package com.github.andreyasadchy.xtra.ui.player

import android.animation.AnimatorInflater
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.databinding.PlayerLayoutBinding
import com.github.andreyasadchy.xtra.util.isTelevision

/** Makes the touch-first player overlay usable with a TV D-pad on API 21+. */
internal fun PlayerLayoutBinding.configureTelevisionControls() {
    if (!root.context.isTelevision()) return

    root.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
    televisionButtons().forEach { control ->
        control.isFocusable = true
        control.isFocusableInTouchMode = false
        control.setBackgroundResource(R.drawable.tv_player_control_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            control.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                root.context,
                R.animator.tv_card_focus,
            )
        }
    }
    progressBar.isFocusable = true
    progressBar.isFocusableInTouchMode = false
}

/**
 * The controller is made visible by an animator. Posting is essential on API 21 because a
 * child cannot take focus while its controller root is still GONE.
 */
internal fun PlayerLayoutBinding.requestTelevisionFocus(preferred: View = playPause) {
    root.post {
        if (!root.isVisible) return@post
        val target = preferred.takeIf { it.canTakeTelevisionFocus() }
            ?: playPause.takeIf { it.canTakeTelevisionFocus() }
            ?: televisionButtons().firstOrNull { it.canTakeTelevisionFocus() }
        target?.requestFocus()
    }
}

/** Explicit row navigation avoids FocusFinder getting trapped in the left bottom container. */
internal fun PlayerLayoutBinding.moveTelevisionFocus(keyCode: Int): Boolean {
    val rows = televisionFocusRows().map { row -> row.filter { it.canTakeTelevisionFocus() } }
    val current = root.findFocus()
    if (current == progressBar && keyCode in setOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT)) {
        return false // Let DefaultTimeBar handle seeking.
    }
    val currentRow = rows.indexOfFirst { row -> row.any { it == current } }
    if (currentRow == -1) {
        requestTelevisionFocus()
        return true
    }
    val row = rows[currentRow]
    val target = when (keyCode) {
        KeyEvent.KEYCODE_DPAD_LEFT -> row.getOrNull(row.indexOf(current) - 1)
        KeyEvent.KEYCODE_DPAD_RIGHT -> row.getOrNull(row.indexOf(current) + 1)
        KeyEvent.KEYCODE_DPAD_UP -> rows.subList(0, currentRow)
            .indexOfLast { it.isNotEmpty() }
            .takeIf { it >= 0 }
            ?.let { targetRow -> nearestHorizontalView(current, rows[targetRow]) }
        KeyEvent.KEYCODE_DPAD_DOWN -> rows.subList(currentRow + 1, rows.size)
            .indexOfFirst { it.isNotEmpty() }
            .takeIf { it >= 0 }
            ?.let { offset -> nearestHorizontalView(current, rows[currentRow + 1 + offset]) }
        else -> null
    }
    target?.requestFocus()
    return true
}

internal fun PlayerLayoutBinding.shouldDelegateTelevisionSeek(keyCode: Int): Boolean =
    progressBar.hasFocus() && keyCode in setOf(KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT)

private fun PlayerLayoutBinding.televisionFocusRows(): List<List<View>> = listOf(
    listOf(minimize, download, follow, sleepTimer, aspectRatio, speed, quality, menu),
    listOf(rewind, playPause, fastForward),
    listOf(
        restart,
        seekLive,
        vodGames,
        volume,
        audioCompressor,
        audioOnly,
        subtitles,
        toggleChatInput,
        toggleChat,
        fullscreen,
    ),
    listOf(progressBar),
)

private fun nearestHorizontalView(source: View, targets: List<View>): View? {
    val sourceLocation = IntArray(2).also(source::getLocationOnScreen)
    val sourceCenter = sourceLocation[0] + source.width / 2
    return targets.minByOrNull { target ->
        val targetLocation = IntArray(2).also(target::getLocationOnScreen)
        kotlin.math.abs((targetLocation[0] + target.width / 2) - sourceCenter)
    }
}

private fun PlayerLayoutBinding.televisionButtons(): List<View> = listOf(
    minimize,
    download,
    follow,
    sleepTimer,
    aspectRatio,
    speed,
    quality,
    menu,
    rewind,
    playPause,
    fastForward,
    restart,
    seekLive,
    vodGames,
    volume,
    audioCompressor,
    audioOnly,
    subtitles,
    toggleChatInput,
    toggleChat,
    fullscreen,
)

private fun View.canTakeTelevisionFocus(): Boolean = isShown && isEnabled && isFocusable

/** Converts noisy Android key-repeat events into predictable accelerated seek steps. */
internal class TvRemoteSeekRepeater {
    private var activeKeyCode = KeyEvent.KEYCODE_UNKNOWN
    private var lastStepTime = 0L

    fun begin(event: KeyEvent) {
        activeKeyCode = event.keyCode
        lastStepTime = event.downTime
    }

    fun getSeekDelta(event: KeyEvent): Long? {
        if (event.action != KeyEvent.ACTION_DOWN || event.repeatCount == 0) return null
        if (event.keyCode != activeKeyCode) begin(event)
        val heldMillis = event.eventTime - event.downTime
        if (heldMillis < LONG_PRESS_START_MILLIS || event.eventTime - lastStepTime < SEEK_REPEAT_MILLIS) {
            return null
        }
        lastStepTime = event.eventTime
        val step = if (heldMillis >= ACCELERATED_SEEK_START_MILLIS) 60_000L else 10_000L
        return if (event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) -step else step
    }

    fun end(event: KeyEvent) {
        if (event.keyCode == activeKeyCode) {
            activeKeyCode = KeyEvent.KEYCODE_UNKNOWN
            lastStepTime = 0L
        }
    }

    companion object {
        private const val LONG_PRESS_START_MILLIS = 600L
        private const val SEEK_REPEAT_MILLIS = 600L
        private const val ACCELERATED_SEEK_START_MILLIS = 5_000L
    }
}
