package com.paranoid.privacylock.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.paranoid.privacylock.BuildConfig
import com.paranoid.privacylock.util.LinePagerIndicatorDecoration
import com.paranoid.privacylock.services.LockService
import com.paranoid.privacylock.R
import com.paranoid.privacylock.util.SimpleViewAdapter
import com.paranoid.privacylock.databinding.FragmentDashboardBinding
import com.paranoid.privacylock.util.getColorCompat
import com.paranoid.privacylock.util.spannableString
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext


@AndroidEntryPoint
class DashboardFragment: Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var lastToastTime: Long = 0
    private val toastCooldown: Long = 2500

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvApplicationVersion.text = BuildConfig.VERSION_NAME
        val adapter = SimpleViewAdapter(
            5,
            R.layout.item_card_view_info
        )

        binding.rvInfoCard.adapter = adapter

        binding.rvInfoCard.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvInfoCard)

        binding.rvInfoCard.addItemDecoration(LinePagerIndicatorDecoration())

        val radioButtons = listOf(
            binding.rbOneMinute,
            binding.rbFiveMinute,
            binding.rbSixtyMinute,
            binding.rbThreeMinute,
            binding.rbThirtyMinute,
            binding.rbFifteenMinutes)

        val mapping = mapOf(
            binding.rbOneMinute.id to 1,
            binding.rbThreeMinute.id to 2,
            binding.rbFiveMinute.id to 3,
            binding.rbFifteenMinutes.id to 5,
            binding.rbThirtyMinute.id to 10,
            binding.rbSixtyMinute.id to 15
        )

        binding.rbOneMinute.text = spannableString("1","\nMinute")
        binding.rbThreeMinute.text = spannableString("2","\nMinutes")
        binding.rbFiveMinute.text = spannableString("3","\nMinutes")
        binding.rbFifteenMinutes.text = spannableString("5","\nMinutes")
        binding.rbThirtyMinute.text = spannableString("10","\nMinutes")
        binding.rbSixtyMinute.text = spannableString("15","\nMinutes")

        dashboardViewModel.selectedButtonId.observe(viewLifecycleOwner) { selectedButtonId ->
            radioButtons.forEach { radioButton ->
                radioButton.isChecked = radioButton.id == selectedButtonId
                radioButton.setTextColor(
                    if (radioButton.isChecked){
                        if (dashboardViewModel.getCurrentMinuteDelay() > 0){
                            ContextCompat.getColor(requireContext(), R.color.orange_dark)
                        }
                        ContextCompat.getColor(requireContext(), R.color.white)
                    }
                    else ContextCompat.getColor(requireContext(), R.color.text_surface)
                )
            }
        }

        radioButtons.forEach { radioButton ->
            radioButton.setOnClickListener {
                if (dashboardViewModel.getToggleVibrateChecked()){
                    val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                    val vibrationEffect = VibrationEffect.createOneShot(
                        1L,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                    vibrator.vibrate(vibrationEffect)
                }
                dashboardViewModel.selectButton(radioButton.id)
                if (dashboardViewModel.isPlayingActionButton.value == false)
                    dashboardViewModel.togglePlayPauseActionButton()
            }
        }


        binding.buttonSettings.setOnClickListener {
            if (dashboardViewModel.getToggleVibrateChecked()){
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                val vibrationEffect = VibrationEffect.createOneShot(
                    1L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
                vibrator.vibrate(vibrationEffect)
            }
            findNavController().navigate(R.id.action_dashboardFragment_to_settingFragment)
        }

        val mPlayToPauseAnim = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.play_to_pause)
        val mPauseToPlayAnim = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.pause_to_play)

        binding.floatingActionButton.setOnClickListener {
            if (dashboardViewModel.getToggleVibrateChecked()){
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
            val selectedRadioButtonId = dashboardViewModel.selectedButtonId.value
            val currentTime = System.currentTimeMillis()

            if (selectedRadioButtonId == null || selectedRadioButtonId == -1 || selectedRadioButtonId == 0) {
                if (currentTime - lastToastTime > toastCooldown) {
                    Toast.makeText(requireContext(), R.string.text_before_start, Toast.LENGTH_SHORT).show()
                    lastToastTime = currentTime
                }
            }
            else {
                dashboardViewModel.togglePlayPauseActionButton()
            }
        }

        dashboardViewModel.isPlayingActionButton.observe(viewLifecycleOwner){ isPlayingAction ->
            val selectedRadioButtonId = dashboardViewModel.selectedButtonId.value
            val selectedRadioButton = radioButtons.find { it.id == selectedRadioButtonId }


            if (isPlayingAction){
                binding.tvHints?.text = ContextCompat.getString(requireContext(),R.string.text_before_start)
                binding.mcInfoFrame.setBackgroundDrawable(null)
                binding.floatingActionButton.setImageDrawable(mPauseToPlayAnim)
                binding.frameLayout.stopShimmer()

                mPauseToPlayAnim!!.start()
                dashboardViewModel.saveCurrentMinuteDelay(0)

                selectedRadioButton?.let {
                    selectedRadioButton.setTextColor(requireContext().getColorCompat(R.color.text_light_white))
                }
                stopLockService(requireContext().applicationContext)
                //selectedRadioButton!!.setTextColor(requireContext().getColorCompat(R.color.text_light_white))

            } else {
                binding.tvHints?.text = ContextCompat.getString(requireContext(),R.string.text_after_start)
                binding.mcInfoFrame.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.green_horizontal_gradient))
                binding.floatingActionButton.setImageDrawable(mPlayToPauseAnim)
                binding.frameLayout.startShimmer()

                mPlayToPauseAnim!!.start()

                selectedRadioButton?.let {
                    dashboardViewModel.saveCurrentMinuteDelay(mapping[it.id]!!)
                    selectedRadioButton.setTextColor(requireContext().getColorCompat(R.color.orange_dark))
                }
                startLockService()
            }
        }
    }

    private fun startLockService(){
                startLockService(
                    context = requireContext().applicationContext,
                    minutesDelay = dashboardViewModel.getCurrentMinuteDelay()
                )
    }

    private fun startLockService(@ApplicationContext context: Context, minutesDelay: Int) {
        val intent = Intent(context, LockService::class.java)
        intent.putExtras(Bundle().apply {
            putInt("minutesDelay", minutesDelay)
        })
        ContextCompat.startForegroundService(context, intent)
    }

    private fun stopLockService(@ApplicationContext context: Context) {
        val intent = Intent(context, LockService::class.java)
        context.stopService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
