package com.example.awaazsetu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val highContrastSwitch = findViewById<SwitchMaterial>(R.id.high_contrast_switch)
        val brightnessSlider = findViewById<Slider>(R.id.brightness_slider)
        val voiceSpeedSlider = findViewById<Slider>(R.id.voice_speed_slider)
        val contactUsButton = findViewById<LinearLayout>(R.id.contact_us_button)

        val sharedPref = getSharedPreferences("SarthiSettings", Context.MODE_PRIVATE)

        // --- Initialize UI from Saved State ---

        highContrastSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        voiceSpeedSlider.value = sharedPref.getFloat("voiceSpeed", 1.0f)

        // --- Set Listeners ---

        highContrastSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        brightnessSlider.addOnChangeListener { _, value, _ ->
            val layoutParams = window.attributes
            layoutParams.screenBrightness = value
            window.attributes = layoutParams
        }

        voiceSpeedSlider.addOnChangeListener { _, value, _ ->
            with(sharedPref.edit()) {
                putFloat("voiceSpeed", value)
                apply()
            }
        }

        contactUsButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("25F2004867@ds.study.iitm.ac.in"))
                putExtra(Intent.EXTRA_SUBJECT, "Sarthi App Feedback")
            }
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
        }
    }

    companion object {
        fun getVoiceSpeed(context: Context): Float {
            val sharedPref = context.getSharedPreferences("SarthiSettings", Context.MODE_PRIVATE)
            return sharedPref.getFloat("voiceSpeed", 1.0f)
        }
    }
}
