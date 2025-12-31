package com.example.awaazsetu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var micButton: ImageButton
    private lateinit var pulseEffect: View
    private lateinit var outputText: TextView
    private lateinit var langToggleGroup: MaterialButtonToggleGroup
    private lateinit var tts: TextToSpeech
    private lateinit var promptsChipGroup: ChipGroup
    private lateinit var promptsLabel: TextView
    private lateinit var settingsButton: ImageButton
    
    // Speech Recognizer (No UI)
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionIntent: Intent

    private var currentLang = "hi" // Hindi default

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            startListening()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------- ACTIVITY ----------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        micButton = findViewById(R.id.micButton)
        pulseEffect = findViewById(R.id.pulseEffect)
        outputText = findViewById(R.id.outputText)
        langToggleGroup = findViewById(R.id.langToggleGroup)
        promptsChipGroup = findViewById(R.id.promptsChipGroup)
        promptsLabel = findViewById(R.id.promptsLabel)
        settingsButton = findViewById(R.id.settings_button)

        tts = TextToSpeech(this, this)
        
        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                outputText.text = if (currentLang == "hi") "सुन रहा हूँ..." else "Listening..."
                startWaveAnimation()
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                // Wave animation is handled by AnimationDrawable loop
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                // Wait for onResults to stop animation, or stop it here if needed immediately
                // We keep wave animation until we process results usually
            }
            override fun onError(error: Int) {
                stopWaveAnimation()
                val errorMsg = if (currentLang == "hi") "फिर से प्रयास करें" else "Try again"
                outputText.text = errorMsg
            }
            override fun onResults(results: Bundle?) {
                stopWaveAnimation()
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    processVoiceCommand(matches[0])
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        langToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val btnHindi = findViewById<MaterialButton>(R.id.btnHindi)
                val btnEnglish = findViewById<MaterialButton>(R.id.btnEnglish)
                
                // Reset colors
                btnHindi.setTextColor(Color.parseColor("#1E293B"))
                btnEnglish.setTextColor(Color.parseColor("#1E293B"))

                when (checkedId) {
                    R.id.btnHindi -> {
                        currentLang = "hi"
                        outputText.text = "माइक दबाएं और बोलें"
                        btnHindi.setTextColor(Color.WHITE) // Selected text color
                    }
                    R.id.btnEnglish -> {
                        currentLang = "en"
                        outputText.text = "Tap mic and speak"
                        btnEnglish.setTextColor(Color.WHITE) // Selected text color
                    }
                }
                loadQuickPrompts()
            }
        }
        
        // Set default selection
        langToggleGroup.check(R.id.btnHindi)

        micButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                startListening()
            }
        }
        
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        loadQuickPrompts()
        startBreathingAnimation()
        
        // Animate Prompts Label
        promptsLabel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))
    }

    override fun onResume() {
        super.onResume()
        // Apply settings when returning from Settings
        if (::tts.isInitialized) {
            tts.setSpeechRate(SettingsActivity.getVoiceSpeed(this))
        }
    }

    private fun loadQuickPrompts() {
        promptsChipGroup.removeAllViews()
        
        // Use the fixed curated list from CommandRepository for stable UI
        val prompts = CommandRepository.quickPrompts
        
        for (prompt in prompts) {
            val chip = Chip(this)
            
            // Set text based on current language
            chip.text = if (currentLang == "hi") prompt.labelHi else prompt.labelEn
            
            chip.isClickable = true
            chip.isCheckable = false
            chip.setChipBackgroundColorResource(R.color.chip_bg_color)
            chip.chipStrokeWidth = 2f
            chip.setChipStrokeColorResource(R.color.chip_stroke_color)
            chip.setTextColor(ContextCompat.getColor(this, R.color.text_slate_dark))
            
            chip.setOnClickListener {
                processVoiceCommand(prompt.query)
            }
            
            promptsChipGroup.addView(chip)
        }
    }

    // ---------------- SPEECH INPUT ----------------
    private fun startListening() {
        micButton.clearAnimation() // Stop breathing
        
        recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (currentLang == "hi") "hi-IN" else "en-IN")
        }

        try {
            speechRecognizer.startListening(recognitionIntent)
        } catch (e: Exception) {
            stopWaveAnimation()
            Toast.makeText(this, "Recognition not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBreathingAnimation() {
        micButton.setImageResource(R.drawable.ic_mic)
        val breathingAnim = AnimationUtils.loadAnimation(this, R.anim.breathing)
        micButton.startAnimation(breathingAnim)
    }
    
    private fun startWaveAnimation() {
        pulseEffect.visibility = View.VISIBLE
        pulseEffect.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_ripple))
        
        // Switch to wave animation
        micButton.setImageResource(R.drawable.anim_wave_list)
        val frameAnimation = micButton.drawable as AnimationDrawable
        frameAnimation.start()
    }
    
    private fun startSpeakingAnimation() {
        pulseEffect.visibility = View.GONE // Hide ripple when speaking
        pulseEffect.clearAnimation()
        
        // Switch to speaker animation
        micButton.setImageResource(R.drawable.anim_listen_list)
        val frameAnimation = micButton.drawable as AnimationDrawable
        frameAnimation.start()
    }
    
    private fun stopWaveAnimation() {
        pulseEffect.clearAnimation()
        pulseEffect.visibility = View.GONE
        
        // Stop wave/speaker and go back to mic
        if (micButton.drawable is AnimationDrawable) {
            (micButton.drawable as AnimationDrawable).stop()
        }
        startBreathingAnimation()
    }

    private fun processVoiceCommand(spokenText: String) {
        // Use the optimized search function from CommandRepository
        val command = CommandRepository.findCommand(spokenText)

        val reply = when {
            command != null && currentLang == "hi" -> command.hi
            command != null && currentLang == "en" -> command.en
            currentLang == "hi" -> "माफ़ कीजिए, मैं इसे नहीं समझ पाया। कृपया 'मदद' बोलें।"
            else -> "Sorry, I didn't understand that. Please say 'Help'."
        }

        outputText.text = reply
        
        // Determine utterance ID for action (Confirm Dialing after speech)
        // Format: "CONFIRM_DIAL:number:name"
        val utteranceId = if (command != null && command.actionType == ActionType.DIAL && command.actionData.isNotEmpty()) {
            "CONFIRM_DIAL:${command.actionData}:${command.actionLabel}"
        } else {
            "MSG_${System.currentTimeMillis()}"
        }
        
        speak(reply, utteranceId)
    }

    private fun showCallConfirmationDialog(number: String, name: String) {
        val title = if (currentLang == "hi") "कॉल करें?" else "Call?"
        val message = if (currentLang == "hi") "$name ($number)" else "$name ($number)"
        val yesText = if (currentLang == "hi") "हाँ" else "Yes"
        val noText = if (currentLang == "hi") "नहीं" else "No"

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_mic) 
            .setPositiveButton(yesText) { dialog, _ ->
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$number")
                }
                startActivity(dialIntent)
                dialog.dismiss()
            }
            .setNegativeButton(noText) { dialog, _ ->
                val cancelMsg = if (currentLang == "hi") "कॉल रद्द कर दी गई।" else "Call cancelled."
                speak(cancelMsg, "MSG_CANCEL")
                outputText.text = cancelMsg
                dialog.dismiss()
            }
            .show()
    }

    // ---------------- TTS ----------------
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setSpeechRate(SettingsActivity.getVoiceSpeed(this))
            tts.setPitch(1.0f)
            
            // Set up listener to handle actions after speaking
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    runOnUiThread {
                        startSpeakingAnimation()
                    }
                }

                override fun onDone(utteranceId: String?) {
                    runOnUiThread {
                        stopWaveAnimation() // Back to breathing (default) state
                    }
                    
                    if (utteranceId?.startsWith("CONFIRM_DIAL:") == true) {
                        val parts = utteranceId.split(":")
                        if (parts.size >= 3) {
                            val phoneNumber = parts[1]
                            val serviceName = parts[2]
                            
                            // Launch dialog on UI thread
                            runOnUiThread {
                                try {
                                    showCallConfirmationDialog(phoneNumber, serviceName)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                    runOnUiThread {
                        stopWaveAnimation()
                    }
                }
            })
        }
    }

    private fun speak(text: String, utteranceId: String) {
        val locale = if (currentLang == "hi") Locale.forLanguageTag("hi-IN") else Locale.forLanguageTag("en-IN")
        tts.language = locale
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        tts.stop()
        tts.shutdown()
    }
}
