package com.github.andreyasadchy.xtra.ui.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.andreyasadchy.xtra.R
import com.github.andreyasadchy.xtra.databinding.DialogGamesSortBinding
import com.github.andreyasadchy.xtra.repository.datasource.GamesDataSource.Companion.SORT_RECENT
import com.github.andreyasadchy.xtra.repository.datasource.GamesDataSource.Companion.SORT_RECOMMENDED
import com.github.andreyasadchy.xtra.repository.datasource.GamesDataSource.Companion.SORT_VIEWERS
import com.github.andreyasadchy.xtra.repository.datasource.GamesDataSource.Companion.SORT_VIEWERS_ASC
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GamesSortDialog : BottomSheetDialogFragment() {

    interface OnSort {
        fun onSortChanged(sort: String)
    }

    companion object {
        private const val SORT = "sort"

        fun newInstance(sort: String): GamesSortDialog {
            return GamesSortDialog().apply {
                arguments = Bundle().apply {
                    putString(SORT, sort)
                }
            }
        }
    }

    private var _binding: DialogGamesSortBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: OnSort

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as OnSort
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogGamesSortBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        with(binding) {
            sort.check(
                when (requireArguments().getString(SORT)) {
                    SORT_VIEWERS -> R.id.viewers_high
                    SORT_VIEWERS_ASC -> R.id.viewers_low
                    SORT_RECENT -> R.id.recent
                    else -> R.id.recommended
                }
            )
            recommended.setOnClickListener { selectSort(SORT_RECOMMENDED) }
            viewersHigh.setOnClickListener { selectSort(SORT_VIEWERS) }
            viewersLow.setOnClickListener { selectSort(SORT_VIEWERS_ASC) }
            recent.setOnClickListener { selectSort(SORT_RECENT) }
            view.post {
                recommended.requestFocus()
            }
        }
    }

    private fun selectSort(sort: String) {
        if (requireArguments().getString(SORT) != sort) {
            listener.onSortChanged(sort)
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
