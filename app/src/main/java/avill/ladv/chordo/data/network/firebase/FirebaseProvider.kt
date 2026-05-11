package avill.ladv.chordo.data.network.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import avill.ladv.chordo.data.network.firebase.entities.MultipleResource


class FirebaseProvider(keepSynced: Boolean, reference: String) {
    var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = db.getReference(reference)
    init {
        //this element works offline
        databaseReference.keepSynced(keepSynced)

    }
    fun setValue(key: String,deviceActions: MultipleResource?) {
        databaseReference.child(key).setValue(deviceActions).addOnSuccessListener {
            Log.v(FirebaseProvider::class.simpleName, "setValue: success")
        }
    }
    fun add(emp: String): Task<Void> {
        return databaseReference.push().setValue(emp)
    }
    fun remove(key: String?): Task<Void> {
        return databaseReference.child(key!!).removeValue()
    }
    fun update(key: String, hashMap: HashMap<String?, Any?>): Task<Void> {
        return databaseReference.child(key).updateChildren(hashMap)
    }
    operator fun get(key: String?): Query {
        return if (key == null) {
            databaseReference.orderByKey()
        } else
            databaseReference.orderByKey().startAfter(key)
    }
    fun get(): Query {
        return databaseReference
    }
    //only one time
    fun setForSingleValueListener() {
        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.v(
                            FirebaseProvider::class.simpleName,
                            "onDataChange: " + dataSnapshot.key
                        )
                    } else {
                        Log.e(FirebaseProvider::class.simpleName, "onDataChange: no data found")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                    Log.e(
                        FirebaseProvider::class.simpleName,
                        "onCancelled: ",
                        databaseError.toException()
                    )
                }
            })
    }
    //every time
    fun eventListener(){
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.v(FirebaseProvider::class.simpleName, "onChildAdded: " + snapshot.value)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.v(FirebaseProvider::class.simpleName, "onChildChanged: " + snapshot.value)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.v(FirebaseProvider::class.simpleName, "onChildRemoved: " + snapshot.value)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.v(FirebaseProvider::class.simpleName, "onChildMoved: " + snapshot.value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.v(FirebaseProvider::class.simpleName, "onCancelled: " + error.message)
            }
        })
    }
    fun setListener(child: String) {
        databaseReference.child(child)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.v(FirebaseProvider::class.simpleName, "onDataChange: " + dataSnapshot.value)
                    } else {
                        Log.e(FirebaseProvider::class.simpleName, "onDataChange: no data found")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                    Log.e(FirebaseProvider::class.simpleName, "onCancelled: ", databaseError.toException())
                }
            })
    }
}