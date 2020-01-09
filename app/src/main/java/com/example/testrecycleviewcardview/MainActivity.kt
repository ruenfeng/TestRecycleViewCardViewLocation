package com.example.testrecycleviewcardview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val REQUIRED = "Required"



    //access database table
    private var locationDatabase: DatabaseReference? = null
    //to get the current database pointer
    private var locationReference: DatabaseReference? = null
    private var locationListener: ChildEventListener? = null

    //no need
    private var locationAdapter: FirebaseRecyclerAdapter<Location, LocationViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //to get the root folder
        locationDatabase = FirebaseDatabase.getInstance().reference
        //to access to the table
        locationReference = FirebaseDatabase.getInstance().getReference("location")

        firebaseListenerInit()
        locationRecycleView.layoutManager = LinearLayoutManager(this)
        val query = locationReference!!.limitToLast(8)
        locationAdapter = object: FirebaseRecyclerAdapter<Location, LocationViewHolder>(
            Location::class.java, R.layout.location_layout, LocationViewHolder::class.java,query
        ){
            override fun populateViewHolder(viewHolder: LocationViewHolder?, model: Location?, position: Int) {
                viewHolder!!.bindLocation(model)
            }

            override fun onChildChanged(
                type: ChangeEventListener.EventType?,
                snapshot: DataSnapshot?,
                index: Int,
                oldIndex: Int
            ) {
                super.onChildChanged(type, snapshot, index, oldIndex)
                locationRecycleView.scrollToPosition(index)
            }
        }
        locationRecycleView.adapter = locationAdapter

    }

    private fun firebaseListenerInit() {
        val childEventListener = object: ChildEventListener{
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "postMessages:onCancelled", databaseError!!.toException())
                //Toast.makeText(this, "Failed to load Message.", Toast.LENGTH_SHORT).show()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, location: String?) {
                Log.e(TAG, "onChildMoved:" + dataSnapshot!!.key)

                // A location has changed position
                val location = dataSnapshot.getValue(Location::class.java)
                //toast here
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, location: String?) {
                Log.e(TAG, "onChildChanged: " + dataSnapshot!!.key)

                val location = dataSnapshot.getValue(Location::class.java)
                //toast here
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, location: String?) {
                val location = dataSnapshot!!.getValue(Location::class.java)

                Log.e(TAG, "onChildAdded:" + location!!)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.e(TAG, "onChildRemoved:" + dataSnapshot!!.key)

                // A message has been removed
                val location = dataSnapshot.getValue(Location::class.java)
            }
        }
    }
    override fun onStop() {
        super.onStop()

        if (locationListener != null) {
            locationReference!!.removeEventListener(locationListener!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        locationAdapter!!.cleanup()
    }
    private fun addNewLocation(){
        val location = Location("IPC"
            , "Jln. SS22/47, SL 47400, Damansara Jaya"
            ,"https://firebasestorage.googleapis.com/v0/b/greenurth-1dee5.appspot.com/o/ikano-power-center-recycle-center.jpg?alt=media&token=dc972945-4333-417b-badd-0efe9723cdcd"
            ,"Sunday 10AM - 12PM")

        val locationValues = location.toMap()
        val childUpdates = HashMap<String, Any>()

        val key = locationDatabase!!.child("location").push().key

        childUpdates.put("/location/" + key, locationValues)

        locationDatabase!!.updateChildren(childUpdates)
    }
}
