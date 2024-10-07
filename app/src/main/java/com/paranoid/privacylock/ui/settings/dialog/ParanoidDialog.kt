package com.paranoid.privacylock.ui.settings.dialog

import android.app.Dialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.airbnb.lottie.RenderMode
import com.paranoid.privacylock.BuildConfig
import com.paranoid.privacylock.receivers.MyAdminReceiver
import com.paranoid.privacylock.R
import com.paranoid.privacylock.databinding.FragmentDialogBinding
import com.paranoid.privacylock.util.dpToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParanoidDialog:DialogFragment(R.layout.fragment_dialog) {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!
    private val dialogViewModel: DialogViewModel by viewModels()

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(it.attributes)
            layoutParams.gravity = Gravity.TOP
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.y = 64.dpToPx(requireContext()) //TODO set for diff orientations
            it.attributes = layoutParams
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDialogBinding.inflate(inflater, container, false)

        binding.paranoidLottie.renderMode = RenderMode.HARDWARE
        binding.paranoidLottie.setAnimation("lottielab-json-to-dotlottie.lottie")
        binding.paranoidLottie.playAnimation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileNavigationView.menu.getItem(2).title = "Version           ${BuildConfig.VERSION_NAME}"
        binding.profileNavigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_share_app -> {
                    if (dialogViewModel.getToggleVibrateChecked()){
                        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        val vibrationEffect = VibrationEffect.createOneShot(
                            1L,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                        vibrator.vibrate(vibrationEffect)
                    }
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.setType("text/plain")
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        var shareMessage = getString(R.string.sharing_text)
                        shareMessage =
                            shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))
                    true
                }
                R.id.menu_dev_person -> {
                    if (dialogViewModel.getToggleVibrateChecked()){
                        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        val vibrationEffect = VibrationEffect.createOneShot(
                            1L,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                        vibrator.vibrate(vibrationEffect)
                    }
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hardalgorithm.github.io"))
                    startActivity(browserIntent)
                    true
                }
                R.id.menu_delete_app -> {
                    if (dialogViewModel.getToggleVibrateChecked()){
                        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                        val vibrationEffect = VibrationEffect.createOneShot(
                            1L,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                        vibrator.vibrate(vibrationEffect)
                          }
                    try {
                        val devicePolicyManager =
                            requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
                        val componentName = ComponentName(requireContext(), MyAdminReceiver::class.java)
                        devicePolicyManager.removeActiveAdmin(componentName)


                        val intent = Intent(Intent.ACTION_DELETE).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    }catch (e: Exception){
                        Toast.makeText(requireContext(), "App can't be deleted", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
        binding.btnExitDialog.setOnClickListener {
            if (dialogViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                val vibrationEffect = VibrationEffect.createOneShot(
                    1L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(vibrationEffect)            }
            dismiss()
        }

        binding.privacyButton.setOnClickListener {
            if (dialogViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                val vibrationEffect = VibrationEffect.createOneShot(
                    1L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(vibrationEffect)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hardalgorithm.github.io/privacy"))
            startActivity(browserIntent)
        }

    }
}