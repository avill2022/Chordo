package avill.ladv.chordo.data.network.firebase

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth


class AuthProvider {
    var firebaseAuth: FirebaseAuth

    init {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun logout() {
        if (firebaseAuth.currentUser != null) firebaseAuth.currentUser!!.delete()
    }

    fun currentUserExist(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun logIn(email: String?, pass: String?, completeListener: OnCompleteListener<AuthResult>) {
        firebaseAuth.signInWithEmailAndPassword(email!!, pass!!)
            .addOnCompleteListener(completeListener).addOnFailureListener { e ->
                Log.d(
                    AuthProvider::class.java.simpleName,
                    e.message!!
                )
            }
    }

    fun loginAnonymous(completeListener: OnCompleteListener<AuthResult>) {
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(completeListener)
    }

    fun createUserWithEmailAndPassword(
        email: String?,
        pass: String?,
        completeListener: OnCompleteListener<AuthResult>
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email!!, pass!!)
            .addOnCompleteListener(completeListener)
    }

    fun sendEmailVerification(completeListener: OnCompleteListener<Void>) {
        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
            .addOnCompleteListener(completeListener)
    }
}


