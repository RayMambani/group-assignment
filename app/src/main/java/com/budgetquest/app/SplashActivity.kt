package com.budgetquest.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var dot1: View
    private lateinit var dot2: View
    private lateinit var dot3: View

    private val handler = Handler(Looper.getMainLooper())
    private var currentDot = 0

    private val runnable = object : Runnable {
        override fun run() {
            updateDots()
            currentDot = (currentDot + 1) % 3
            handler.postDelayed(this, 400)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        dot1 = findViewById(R.id.dot1)
        dot2 = findViewById(R.id.dot2)
        dot3 = findViewById(R.id.dot3)

        handler.post(runnable)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3000)
    }

    private fun updateDots() {
        dot1.setBackgroundResource(R.drawable.dot_inactive)
        dot2.setBackgroundResource(R.drawable.dot_inactive)
        dot3.setBackgroundResource(R.drawable.dot_inactive)

        when (currentDot) {
            0 -> dot1.setBackgroundResource(R.drawable.dot_active)
            1 -> dot2.setBackgroundResource(R.drawable.dot_active)
            2 -> dot3.setBackgroundResource(R.drawable.dot_active)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}
