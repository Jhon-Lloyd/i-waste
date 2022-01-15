package com.example.i_waste

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.i_waste.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    //viewbinding
    private lateinit var binding: ActivityLoginBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //Progressdialog
    private lateinit var progressDialog: ProgressDialog
    private lateinit var googleSignInClient : GoogleSignInClient
    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    private var password = ""

    private lateinit var analytics: FirebaseAnalytics

    //constants
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //configure Action Bar

        //Configure the Google SignIn
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        //configure Progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Login In...")
        progressDialog.setCanceledOnTouchOutside(false)


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
        //Google Signin Button, Click to begin gsignin
        binding.gsignbtn.setOnClickListener {
            //begin google signin
            Log.d(TAG,"onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from launching the intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityReslt:Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //google signin success now auth with firebase
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }
            catch (e: Exception){
                //failed Google SignIn

                Log.d(TAG,"on ActivityResult: ${e.message}")
            }

        }
    }
    private fun firebaseAuthWithGoogleAccount(account : GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: Begin firebase auth with google account")

        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //login success
                Log.d(TAG, "firebaseAuthWithGoogleAccount:LoggedIn")

                //get LoggedIn User
                val firebaseUser = firebaseAuth.currentUser

                //get user info
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email

                Log.d(TAG, "firebaseAuthWithGoogleAccount:Uid $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount:Email $email")

                //check if user is new or existing
                if (authResult.additionalUserInfo!!.isNewUser) {
                    //user is new - Account created
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created....\n$email")
                    Toast.makeText(
                        this@LoginActivity,
                        "Account created...\n$email",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    //existing use- Logged in
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing created....\n$email")
                    Toast.makeText(
                        this@LoginActivity,
                        "LoggedIn created...\n$email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //start profile activity
                startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Logging Failed due to ${e.message}")
                Toast.makeText(
                    this@LoginActivity,
                    "Logging Failed due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}