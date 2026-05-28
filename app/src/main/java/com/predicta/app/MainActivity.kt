package com.predicta.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import com.predicta.app.ui.PredictaScaffold
import com.predicta.app.ui.theme.PredictaTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: AppSettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromIntro = intent.getBooleanExtra("from_intro", false)
        if (!fromIntro) {
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {
            val settings by settingsRepository.settings.collectAsStateWithLifecycle()

            PredictaTheme(themeMode = settings.themeMode) {
                PredictaScaffold(fromIntro = fromIntro)
            }
        }
    }
}
