package com.paranoid.privacylock.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import com.paranoid.privacylock.R
import com.paranoid.privacylock.shortcut.ShortcutsManager
import com.paranoid.privacylock.databinding.ActivityMainBinding
import com.paranoid.privacylock.util.enableLockdown
import com.paranoid.privacylock.util.isDeviceAdminActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen().apply {
                setKeepOnScreenCondition{
                    !mainViewModel.isFinishedSplashAnimated.value
                }
            }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.parseColor("#801b1b1b"),
                Color.parseColor("#801b1b1b")
            )
        )

        createShortcuts(this)

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragmentContentMain)
        { v, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right,
                bottom = insets.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

    }

    override fun onResume() {
        super.onResume()
        if (!isDeviceAdminActive(this@MainActivity)) {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.action_global_adminPermissionFragment)
        }else{
            if (intent.getBooleanExtra("locked",false)){
                finishAndRemoveTask()
                enableLockdown(this)
            }
        }
    }

    fun createShortcuts(context: Context){
        val shortcutManager = ShortcutsManager(context)
        shortcutManager.createShortcuts()
    }
}