package com.predicta.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import org.koin.android.ext.android.inject

@OptIn(UnstableApi::class)
class IntroActivity : ComponentActivity() {

    private val settingsRepository: AppSettingsRepository by inject()
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary: always show intro on every launch
        /*
        if (settingsRepository.isIntroSeen()) {
            startMainActivity()
            return
        }
        */

        // Hide system bars for immersive video playback
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContentView(R.layout.activity_intro)

        val playerView = findViewById<PlayerView>(R.id.introPlayer)
        val exoPlayer = ExoPlayer.Builder(this).build()
        player = exoPlayer
        playerView.player = exoPlayer

        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.intro_video}")
        val mediaItem = MediaItem.fromUri(videoUri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    completeIntro()
                }
            }
        })
    }

    private fun completeIntro() {
        settingsRepository.setIntroSeen(true)
        startMainActivity()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("from_intro", true)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
