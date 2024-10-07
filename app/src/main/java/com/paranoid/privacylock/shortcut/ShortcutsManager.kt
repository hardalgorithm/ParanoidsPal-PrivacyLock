package com.paranoid.privacylock.shortcut

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.paranoid.privacylock.R
import com.paranoid.privacylock.ui.MainActivity


class ShortcutsManager(private val context: Context) {

    companion object {
        const val SHORTCUT_ONE_ID = "shortcut_one_id"
    }

    fun createShortcuts() {

            val listOfShortcuts = getShortcutsList()
            try {
                ShortcutManagerCompat.setDynamicShortcuts(context, listOfShortcuts)

            } catch (e: Exception) {
                Log.e("ShortcutsManager", "Error creating shortcuts", e)
            }

    }

    private fun getShortcutsList(): List<ShortcutInfoCompat> {
        val shortcutOne = buildShortcut(
            id = SHORTCUT_ONE_ID,
            shortLabel = context.getString(R.string.shortcut_lock_short_label),
            longLabel = context.getString(R.string.shortcut_lock_long_label),
            intent = Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtras(Bundle().apply {
                    putBoolean("locked", true)
                })
            },
            shortcutIcon = R.drawable.ic_lock_48px
        )
        return listOf(shortcutOne)
    }

    private fun buildShortcut(
        id: String,
        shortLabel: String,
        longLabel: String,
        intent: Intent,
        shortcutIcon: Int
    ): ShortcutInfoCompat {
        return ShortcutInfoCompat.Builder(context, id)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setIntent(intent)
            .setIcon(IconCompat.createWithResource(context, shortcutIcon))
            .build()
    }
}
