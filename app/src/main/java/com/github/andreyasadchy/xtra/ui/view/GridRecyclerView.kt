package com.github.andreyasadchy.xtra.ui.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.util.C
import com.github.andreyasadchy.xtra.util.isTelevision
import com.github.andreyasadchy.xtra.util.prefs

class GridRecyclerView : RecyclerView {

    companion object {
        private val televisionFocusPositions = mutableMapOf<String, Int>()
        private val focusIdentityArguments = arrayOf(
            "gameId",
            "gameSlug",
            "channelId",
            "channelLogin",
            "teamName",
            "videoId",
        )
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val prefs = context.prefs()
    private val material3 = prefs.getBoolean(C.UI_THEME_MATERIAL3, true)
    private val portraitColumns = prefs.getString(C.PORTRAIT_COLUMN_COUNT, "1")!!.toInt()
    private val landscapeColumns = prefs.getString(C.LANDSCAPE_COLUMN_COUNT, "2")!!.toInt()

    private val gridLayoutManager: GridLayoutManager
    private var televisionDestinationKey: String? = null

    init {
        val columns = getColumnsForConfiguration(resources.configuration)
        gridLayoutManager = GridLayoutManager(context, columns)
        layoutManager = gridLayoutManager
        addItemDecoration(columns)
        if (context.isTelevision()) {
            descendantFocusability = FOCUS_AFTER_DESCENDANTS
            isFocusable = false
            preserveFocusAfterLayout = true
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (context.isTelevision()) {
            televisionDestinationKey = currentTelevisionDestinationKey()
        }
    }

    override fun requestChildFocus(child: View, focused: View) {
        super.requestChildFocus(child, focused)
        if (!context.isTelevision()) return
        val position = getChildAdapterPosition(child)
        val key = televisionDestinationKey ?: currentTelevisionDestinationKey()
        if (position != NO_POSITION && key != null) {
            televisionFocusPositions[key] = position
        }
    }

    /** Focuses the first result on initial load, or the card that opened the child page on return. */
    fun requestTelevisionContentFocus() {
        if (!context.isTelevision()) return
        val key = televisionDestinationKey ?: currentTelevisionDestinationKey() ?: return
        if (key != currentTelevisionDestinationKey()) return
        val requestedPosition = televisionFocusPositions[key] ?: 0
        post {
            if (key != currentTelevisionDestinationKey() || adapter?.itemCount == 0) return@post
            val targetPosition = requestedPosition.coerceIn(0, (adapter?.itemCount ?: 1) - 1)
            scrollToPosition(targetPosition)
            post {
                if (key != currentTelevisionDestinationKey()) return@post
                val target = findViewHolderForAdapterPosition(targetPosition)?.itemView
                    ?: findViewHolderForAdapterPosition(0)?.itemView
                    ?: getChildAt(0)
                target?.requestFocus()
            }
        }
    }

    private fun currentTelevisionDestinationKey(): String? {
        return try {
            val entry = findNavController().currentBackStackEntry ?: return null
            buildString {
                append(entry.destination.id)
                appendArguments(entry.arguments)
            }
        } catch (_: IllegalStateException) {
            null
        }
    }

    private fun StringBuilder.appendArguments(arguments: Bundle?) {
        focusIdentityArguments.forEach { name ->
            arguments?.getString(name)?.takeIf { it.isNotBlank() }?.let { value ->
                append(':')
                append(name)
                append('=')
                append(value)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!material3) {
            removeItemDecorationAt(0)
        }
        val columns = getColumnsForConfiguration(newConfig)
        gridLayoutManager.spanCount = columns
        addItemDecoration(columns)
    }

    private fun getColumnsForConfiguration(configuration: Configuration): Int {
        return if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitColumns
        } else {
            landscapeColumns
        }
    }

    private fun addItemDecoration(columns: Int) {
        if (!material3) {
            addItemDecoration(
                if (columns <= 1) {
                    DividerItemDecoration(context, GridLayoutManager.VERTICAL)
                } else {
                    MarginItemDecoration(context.resources.getDimension(R.dimen.divider_margin).toInt(), columns)
                }
            )
        }
    }
}
