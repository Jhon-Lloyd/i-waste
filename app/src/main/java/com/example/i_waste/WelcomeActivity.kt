package com.example.i_waste

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.i_waste.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defButton.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, DefinitionActivity::class.java))
        }

        binding.getStartedbutton.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, ScanActivity::class.java))
        }

        binding.profilebutton.setOnClickListener {
            startActivity(Intent(this@WelcomeActivity, ProfileActivity::class.java))
        }
    }
}