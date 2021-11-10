package com.example.i_waste

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.i_waste.databinding.ActivityProfileBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class ProfileActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics

    //view binding
    private lateinit var binding: ActivityProfileBinding

    //Actionbar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure Action
        actionBar = supportActionBar!!
        actionBar.title = "Profile"

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        analytics = Firebase.analytics

        //handle click , log out
        binding.Logout.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //use not null, user is logged in, get user info
            val email = firebaseUser.email
            //set to textview
            binding.email11.text = email
        }
    }
}
