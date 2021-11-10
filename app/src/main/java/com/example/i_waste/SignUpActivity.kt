package com.example.i_waste

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.i_waste.databinding.ActivitySignUpBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    private lateinit var database :DatabaseReference
    private lateinit var analytics: FirebaseAnalytics
    //view binding
    private lateinit var binding: ActivitySignUpBinding
    //Action bar
    private lateinit var actionBar: ActionBar
    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog
    //FirebateAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var name = ""
    private var email = ""
    private var password = ""
    private var cpassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure Actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //configure dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Creating Account In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        //handle click, begin signup
        binding.signupButton.setOnClickListener {
            //validate data
            validate()

            analytics = Firebase.analytics
        }
    }

    private fun validate() {
        //get data
        email = binding.email2.text.toString().trim()
        name = binding.name2.text.toString().trim()
        password = binding.password2.text.toString().trim()
        cpassword = binding.cPassword2.text.toString().trim()

        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.email2.error = "Invalid Email Format"
        }
        else if (TextUtils.isEmpty(password)){
            //password isn't entered
            binding.password2.error ="Please Enter Password"
        }
        else if (TextUtils.isEmpty(name)){
            binding.name2.error = "Please Enter Name"
        }
        else if (TextUtils.isEmpty(cpassword)){
            binding.cPassword2.error = "Please Confirm Password"
        }
        else if (password.length<6){
            //password length is less than 6
            binding.password2.error = "Password must atleast 6 characters long"
        }
        else{
            //data is valid. continue signup
            firebaseSignUp()
        }



    }

    private fun firebaseSignUp() {
        //show progressdialog
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //signup success
                progressDialog.dismiss()
                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Account Created with email $email",Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener{ e->
                //  signup failed
                progressDialog.dismiss()
                Toast.makeText(this, "SignUp Failed due to ${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity
        return super.onSupportNavigateUp()
    }
}


