package com.example.tataaignewsapp

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tataaignewsapp.databinding.ActivityMainBinding
import com.example.tataaignewsapp.databinding.DialogSettingsBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottomNavBar.setupWithNavController(findNavController(R.id.rootFragment))
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val selectedThemeMode = sharedPreferences.getInt(PREF_SELECTED_THEME, 2)
        setThemeMode(selectedThemeMode)
        binding.toolbar.imgSetting.setOnClickListener {

            dialogSettingsFilter(this, selectedThemeMode) {
                val editor = sharedPreferences.edit()
                editor.putInt(PREF_SELECTED_THEME, it)
                editor.commit()
                setThemeMode(it)
            }
        }


    }

    private fun dialogSettingsFilter(
        context: Context, selectedItem: Int, onApply: ((itemNumber: Int) -> Unit)
    ) {
        val builder = AlertDialog.Builder(context).create()
        LayoutInflater.from(context)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))  // for transparent background
        val binding: DialogSettingsBinding =
            DialogSettingsBinding.inflate(LayoutInflater.from(context))
        builder.setCancelable(false)

        when(selectedItem) {
            0 -> binding.rbLightMode.isChecked = true
            1 -> binding.rbDarkMode.isChecked = true
            2 -> binding.rbSystemDefault.isChecked = true
            else -> {
                binding.rbLightMode.isChecked = false
                binding.rbDarkMode.isChecked = false
                binding.rbSystemDefault.isChecked = true
            }
        }
        binding.btnCancel.setOnClickListener {
            builder.cancel()
        }
        binding.btnApply.setOnClickListener {

            when(binding.radioGroup.checkedRadioButtonId) {
                R.id.rbLightMode -> onApply.invoke(0)
                R.id.rbDarkMode -> onApply.invoke(1)
                R.id.rbSystemDefault -> onApply.invoke(2)
                else -> {
                    onApply.invoke(2)
                }
            }

            builder.cancel()
        }


        builder.setView(binding.root)
        builder.show()

    }

    private fun setThemeMode(selectedMode: Int) {
        when (selectedMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}