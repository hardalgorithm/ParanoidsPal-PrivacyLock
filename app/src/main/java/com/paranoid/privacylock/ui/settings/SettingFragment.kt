package com.paranoid.privacylock.ui.settings


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.materialswitch.MaterialSwitch
import com.paranoid.privacylock.R
import com.paranoid.privacylock.services.ShakeService
import com.paranoid.privacylock.databinding.FragmentSettingBinding
import com.paranoid.privacylock.ui.settings.dialog.ParanoidDialog
import com.paranoid.privacylock.util.getColorCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val settingViewModel: SettingViewModel by viewModels()
    private val gestureInterpolator = PathInterpolatorCompat.create(0f, 0f, 0f, 1f)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val predictiveBackMargin = resources.getDimensionPixelSize(R.dimen.predictive_back_margin)
        var initialTouchY = -1f
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }

                override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                    val progress = gestureInterpolator.getInterpolation(backEvent.progress)
                    if (initialTouchY < 0f) {
                        initialTouchY = backEvent.touchY
                    }
                    val progressY = gestureInterpolator.getInterpolation(
                        (backEvent.touchY - initialTouchY) / binding.dashboardCL.height
                    )

                    val maxTranslationX = (binding.dashboardCL.width / 20) - predictiveBackMargin
                    binding.dashboardCL.translationX = progress * maxTranslationX *
                            (if (backEvent.swipeEdge == BackEventCompat.EDGE_LEFT) 1 else -1)

                    val maxTranslationY = (binding.dashboardCL.height / 20) - predictiveBackMargin
                    binding.dashboardCL.translationY = progressY * maxTranslationY

                    val scale = 1f - (0.1f * progress)
                    binding.dashboardCL.scaleX = scale
                    binding.dashboardCL.scaleY = scale
                }

                override fun handleOnBackCancelled() {
                    initialTouchY = -1f
                    binding.dashboardCL.run {
                        translationX = 0f
                        translationY = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                }
            }
        )

        val trackStates = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
            ),
            intArrayOf(
                requireContext().getColorCompat(R.color.colorOnSurface)
            )
        )

        binding.hapticFeedbackToggle.setTrackTintList(trackStates)
        binding.shakeLockToggle.setTrackTintList(trackStates)

        val defaultSwitchColor = binding.hapticFeedbackToggle.thumbTintList

        binding.hapticFeedbackToggle.setupThumbTint(R.color.orange, defaultSwitchColor)
        binding.shakeLockToggle.setupThumbTint(R.color.orange, defaultSwitchColor)

        settingViewModel.isToggleChecked.observe(viewLifecycleOwner) { isChecked ->
            binding.shakeLockToggle.isChecked = isChecked
        }

        settingViewModel.isToggleVibrateChecked.observe(viewLifecycleOwner) { isChecked ->
            binding.hapticFeedbackToggle.isChecked = isChecked
        }

        binding.hapticFeedbackToggle.setOnCheckedChangeListener { _, isChecked ->
            if (!settingViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                }else{
                    val vibrationEffect = VibrationEffect.createOneShot(
                        20L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(vibrationEffect)
                }
            }
            settingViewModel.saveToggleVibrateChecked(isChecked)
        }

        binding.shakeLockToggle.setOnCheckedChangeListener { _, isChecked ->
            if (settingViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
                }else{
                    val vibrationEffect = VibrationEffect.createOneShot(
                        20L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(vibrationEffect)
                }
            }
            settingViewModel.saveToggleChecked(isChecked)
            if (isChecked){
                startShakeService(requireContext().applicationContext)
                binding.flShakeMeasurer.visibility = View.VISIBLE
                binding.seekBar.visibility = View.VISIBLE
            }else{
                stopShakeService(requireContext().applicationContext)
                binding.flShakeMeasurer.visibility = View.GONE
                binding.seekBar.visibility = View.GONE
            }
        }

        settingViewModel.seekBarProgress.observe(viewLifecycleOwner) { progress ->
            binding.seekBar.progress = progress
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (settingViewModel.getToggleVibrateChecked()){
                    val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    val vibrationEffect = VibrationEffect.createOneShot(
                        1L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(vibrationEffect)
                }
                settingViewModel.setSeekBarProgress(progress)
                val intent = Intent("com.paranoid.privacylock.PRIVACYLOCK_SEEKBAR_CHANGE")
                intent.putExtra("progress", progress)
               requireContext().sendBroadcast(intent)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.aboutUsCard.setOnClickListener {
            if (settingViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                val vibrationEffect = VibrationEffect.createOneShot(
                    1L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(vibrationEffect)

            }
                val dialog = ParanoidDialog()
                dialog.show(requireActivity().supportFragmentManager,"paranoidDialog")
        }

        binding.buttonBack.setOnClickListener {
            if (settingViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                val vibrationEffect = VibrationEffect.createOneShot(
                    1L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(vibrationEffect)

            }
            findNavController().popBackStack()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    fun MaterialSwitch.setupThumbTint(
        checkedColor: Int,
        defaultColor: ColorStateList?
    ) {
        setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isChecked) {
                        thumbTintList = ColorStateList.valueOf(context.getColorCompat(checkedColor))
                    }
                    false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    thumbTintList = defaultColor
                    false
                }
                else -> false
            }
        }
    }

    private fun startShakeService(@ApplicationContext context: Context) {
        val intent = Intent(context, ShakeService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }


    private fun stopShakeService(@ApplicationContext context: Context) {
        val intent = Intent(context, ShakeService::class.java)
        context.stopService(intent)
    }

    override fun onPause() {
        super.onPause()
        settingViewModel.saveState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}