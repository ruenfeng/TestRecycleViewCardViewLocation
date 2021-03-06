package com.example.testrecycleviewcardview

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.location_layout.view.*


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

            override fun onBindViewHolder(
                holder: LocationViewHolder,
                position: Int,
                payloads: MutableList<Any>
            ) {
                super.onBindViewHolder(holder, position, payloads)
                val address: String = holder.itemView.textViewLocationAddress.text.toString()

                holder.itemView.textViewLocationAddress.setOnClickListener{

                    val intent = Intent(ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
                    startActivity(intent)
                }
            }
        }
        locationRecycleView.adapter = locationAdapter

        fabAddLocation.setOnClickListener{
            val intent = Intent(this, AddLocation::class.java)

            startActivity(intent)
        }
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
}