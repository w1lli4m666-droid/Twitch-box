package com.github.andreyasadchy.xtra.ui.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/** Lets a media adapter identify the item that owned focus before the player opened. */
interface TelevisionFocusIdentityAdapter {
    fun getTelevisionFocusIdentity(position: Int): String?

    fun findTelevisionFocusPosition(identity: String): Int?
}

/** Restores TV focus to the media card that launched the full-screen player. */
class TelevisionFocusReturnTarget private constructor(
    private val recyclerViewReference: WeakReference<RecyclerView>,
    private val adapterPosition: Int,
    private val contentIdentity: String?,
) {

    fun restore() {
        val recyclerView = recyclerViewReference.get()?.takeIf { it.isAttachedToWindow && it.isShown } ?: return
        val adapter = recyclerView.adapter ?: return
        if (adapter.itemCount > 0) {
            restoreLoadedItem(recyclerView, adapter)
            return
        }

        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = restoreWhenReady()

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = restoreWhenReady()

            private fun restoreWhenReady() {
                if (adapter.itemCount == 0 || !recyclerView.isAttachedToWindow) return
                adapter.unregisterAdapterDataObserver(this)
                restoreLoadedItem(recyclerView, adapter)
            }
        }
        adapter.registerAdapterDataObserver(observer)
        recyclerView.postDelayed({
            try {
                adapter.unregisterAdapterDataObserver(observer)
            } catch (_: IllegalStateException) {
            }
        }, RESTORE_OBSERVER_TIMEOUT_MILLIS)
    }

    private fun restoreLoadedItem(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        val identityPosition = contentIdentity?.let {
            (adapter as? TelevisionFocusIdentityAdapter)?.findTelevisionFocusPosition(it)
        }
        val position = (identityPosition ?: adapterPosition).coerceIn(0, adapter.itemCount - 1)
        recyclerView.scrollToPosition(position)
        requestItemFocus(recyclerView, position, RESTORE_FOCUS_ATTEMPTS)
    }

    private fun requestItemFocus(recyclerView: RecyclerView, position: Int, attemptsRemaining: Int) {
        recyclerView.post {
            if (!recyclerView.isAttachedToWindow || !recyclerView.isShown) return@post
            val target = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
            if (target != null) {
                target.requestFocus()
            } else if (attemptsRemaining > 0) {
                recyclerView.scrollToPosition(position)
                requestItemFocus(recyclerView, position, attemptsRemaining - 1)
            }
        }
    }

    companion object {
        private const val RESTORE_FOCUS_ATTEMPTS = 3
        private const val RESTORE_OBSERVER_TIMEOUT_MILLIS = 5_000L

        fun capture(focusedView: View?): TelevisionFocusReturnTarget? {
            val focused = focusedView ?: return null
            val recyclerView = findParentRecyclerView(focused) ?: return null
            val itemView = recyclerView.findContainingItemView(focused) ?: return null
            val position = recyclerView.getChildAdapterPosition(itemView)
            if (position == RecyclerView.NO_POSITION) return null
            val identity = (recyclerView.adapter as? TelevisionFocusIdentityAdapter)
                ?.getTelevisionFocusIdentity(position)
            return TelevisionFocusReturnTarget(WeakReference(recyclerView), position, identity)
        }

        private fun findParentRecyclerView(view: View): RecyclerView? {
            var current: View? = view
            while (current != null) {
                if (current is RecyclerView) return current
                current = current.parent as? View
            }
            return null
        }
    }
}
