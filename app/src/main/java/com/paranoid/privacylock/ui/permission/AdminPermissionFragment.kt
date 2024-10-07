package com.paranoid.privacylock.ui.permission

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paranoid.privacylock.receivers.MyAdminReceiver
import com.paranoid.privacylock.R
import com.paranoid.privacylock.databinding.FragmentAdminPermissionBinding
import com.paranoid.privacylock.util.isDeviceAdminActive

class AdminPermissionFragment : Fragment() {
    private var _binding: FragmentAdminPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                activity?.finish()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.permissionButton.setOnClickListener {
            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            val vibrationEffect = VibrationEffect.createOneShot(
                1L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(vibrationEffect)
            val componentName = ComponentName(requireContext(), MyAdminReceiver::class.java)
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            requireContext().startActivity(intent)
        }

        binding.privacyButton.setOnClickListener {

            val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

            val vibrationEffect = VibrationEffect.createOneShot(
                1L,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(vibrationEffect)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://hardalgorithm.github.io/privacy"))
            startActivity(browserIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isDeviceAdminActive(requireContext())) {
            findNavController().navigate(R.id.action_global_dashboardFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}