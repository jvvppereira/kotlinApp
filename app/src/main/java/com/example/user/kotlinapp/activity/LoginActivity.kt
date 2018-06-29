package com.example.user.kotlinapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.user.kotlinapp.R
import com.example.user.kotlinapp.dto.Launch
import com.example.user.kotlinapp.dto.User
import com.example.user.kotlinapp.util.FirebaseSettings
import com.example.user.kotlinapp.util.Message
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val mDatabase = FirebaseSettings().getDatabaseInstance()
    private val mFirebaseAuth = FirebaseSettings().getFirebaseAuthInstance()
    val message = Message(this)
    var lastIndex = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        val query = mDatabase.child(FirebaseSettings.USERS)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (user in dataSnapshot.children) {
                        lastIndex = user.key!!.toInt() + 1
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                message.showMessage("Problema de conexão.")
            }
        })


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener{
            doLogin(getEmail(), getPassword())
        }

        firstAccessButton.setOnClickListener {
            createAuthUser(getEmail(), getPassword())
        }
    }

    private fun getEmail() : String {
        return emailEditText.text.toString()
    }

    private fun getPassword() : String {
        return passwordEditText.text.toString()
    }

    private fun doLogin(email: String, password: String) {
        mFirebaseAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    goToMain()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        message.showMessage("E-mail inválido!")
                    } else {
                        message.showMessage("Não foi possível acessar!")
                    }
                }
            }
    }

    private fun createAuthUser(email: String, password: String) {
        mFirebaseAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    message.showMessage("Sucesso ao cadastrar usuário!")

                    val childUpdates = HashMap<String, Object>()

                    childUpdates["${FirebaseSettings.USERS_BAR}$lastIndex"] = User(
                            task.result.user.uid) as Object

                    mDatabase.updateChildren(childUpdates as Map<String, Any>)

                    doLogin(email, password)
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        message.showMessage("Usuário já cadastrado!")
                    } else {
                        message.showMessage("Não foi possível cadastrar o usuário!")
                    }
                }
            }
    }

    private fun getUserLogin(): User {
        return User(mFirebaseAuth!!.currentUser!!.uid)
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userLogin", getUserLogin())
        startActivity(intent)
    }
}
