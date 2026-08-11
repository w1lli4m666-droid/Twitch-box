package com.github.andreyasadchy.xtra.repository

import com.github.andreyasadchy.xtra.db.BookmarksDao
import com.github.andreyasadchy.xtra.db.LocalChannelFollowsDao
import com.github.andreyasadchy.xtra.db.OfflineVideosDao
import com.github.andreyasadchy.xtra.model.ui.LocalChannelFollow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File

class LocalChannelFollowsRepository(
    private val localChannelFollowsDao: LocalChannelFollowsDao,
    private val offlineVideosDao: OfflineVideosDao,
    private val bookmarksDao: BookmarksDao,
) {

    data class FollowChangeVersions(
        val channels: Long = 0L,
        val accountChannels: Long = 0L,
    )

    private val _followChanges = MutableStateFlow(FollowChangeVersions())
    val followChanges = _followChanges.asStateFlow()

    suspend fun getAll() = withContext(Dispatchers.IO) {
        localChannelFollowsDao.getAll()
    }

    suspend fun getById(id: String) = withContext(Dispatchers.IO) {
        localChannelFollowsDao.getById(id)
    }

    suspend fun save(item: LocalChannelFollow) = withContext(Dispatchers.IO) {
        localChannelFollowsDao.insert(item)
        notifyLocalFollowChanged()
    }

    suspend fun delete(item: LocalChannelFollow) = withContext(Dispatchers.IO) {
        localChannelFollowsDao.delete(item)
        notifyLocalFollowChanged()
    }

    fun notifyAccountFollowChanged() {
        _followChanges.update {
            it.copy(
                channels = it.channels + 1L,
                accountChannels = it.accountChannels + 1L,
            )
        }
    }

    private fun notifyLocalFollowChanged() {
        _followChanges.update { it.copy(channels = it.channels + 1L) }
    }

    suspend fun update(item: LocalChannelFollow) = withContext(Dispatchers.IO) {
        localChannelFollowsDao.update(item)
    }

    suspend fun deleteOldImages() = withContext(Dispatchers.IO) {
        localChannelFollowsDao.getAll().forEach { item ->
            item.channelLogo?.let {
                if (it.isNotBlank()
                    && !item.userId.isNullOrBlank()
                    && bookmarksDao.getByUserId(item.userId).isEmpty()
                    && offlineVideosDao.getByUserId(item.userId).isEmpty()
                ) {
                    File(it).delete()
                }
            }
        }
    }
}
