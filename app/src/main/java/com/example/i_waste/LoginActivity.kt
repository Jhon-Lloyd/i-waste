package com.example.i_waste

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.i_waste.databinding.ActivityLoginBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    //viewbinding
    private lateinit var binding: ActivityLoginBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //Progressdialog
    private lateinit var progressDialog: ProgressDialog
    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //configure Action Bar


        //configure Progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Login In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        analytics = Firebase.analytics

        // handle click, open register activity
        binding.SignupHere.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        //handle click, begin login
        binding.loginButton.setOnClickListener {
            //before loging in, validate data
            validate()
        }
    }
    private fun validate(){
        //get data
        email= binding.email2.text.toString().trim()
        password= binding.password2.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid  email format
            binding.email2.error ="Invalid Email Format"
        }
        else if (TextUtils.isEmpty(password)){
            //no password entered
            binding.password2.error = "Please Enter Password"
        }
        else{
            //data is validated begin login
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Logged-in as as $email",Toast.LENGTH_SHORT).show()
                //open profile activity
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun checkUser() {
        //if user is already log in go to profile
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser !=null){
            //user is already logged in
            startActivity(Intent(this, ProfileActivity::class.java ))
            finish()

        }
    }
}