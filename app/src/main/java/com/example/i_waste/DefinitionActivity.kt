package com.example.i_waste

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.i_waste.databinding.ActivityDefinitionBinding

class DefinitionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDefinitionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDefinitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }
}