package com.example.testrecycleviewcardview

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_location.*
import kotlinx.android.synthetic.main.location_layout.*
import java.io.ByteArrayOutputStream
import java.net.URI
import java.util.*

class AddLocation : AppCompatActivity() {

    private var filePath: Uri? = null
    private var uploadFileName: String? = null

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    //access database table
    private var locationDatabase: DatabaseReference? = null
    //to get the current database pointer
    private var locationReference: DatabaseReference? = null
    private var locationListener: ChildEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        imageButtonLocation.setOnClickListener {
            //check runtime permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //always request for permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }else{
                    //permission granted
                    pickImageFromGallery()
                }
            }else{
                pickImageFromGallery()
            }

            //INITIALISE FIREBASESTORAGE
            storage = FirebaseStorage.getInstance()
            storageReference = storage!!.reference

            //INITIALISE FIREBASEDATABASE
            locationDatabase = FirebaseDatabase.getInstance().reference
            //to access to the table
            locationReference = FirebaseDatabase.getInstance().getReference("location")
            buttonSave.setOnClickListener {
                //uploadFile()//FirebaseNoSignedInUserException
                addNewLocation()
            }
        }

        //upload image to firebase and get the url
        //input the image thing all as one class
        //then do the mapping as what input the db at the main activity
    }
    private fun addNewLocation(){
        //val locUrl:Uri? = storageReference.child("locImages/$uploadFileName").getDownloadUrl().getResult()
        val locationName:String = editTextLocationName.text.toString()
        val locationAddress: String = editTextLocationAddress.text.toString()
        val locationImage: String = "https://ih0.redbubble.net/image.524527453.3004/flat,1000x1000,075,f.u1.jpg"
        val locationOperation: String = editTextLocationOperation.text.toString()
        val location = Location(locationName
            , locationAddress
            , locationImage
            ,locationOperation)

        val locationValues = location.toMap()
        val childUpdates = HashMap<String, Any>()

        //ADD NEW ENTITY
        val key = locationDatabase!!.child("location").push().key

        //ADD THE INTO THE NEWLY CREATED ENTITY
        childUpdates.put("/location/$key", locationValues)

        locationDatabase!!.updateChildren(childUpdates)
        Toast.makeText(this, "Successfully added new location", Toast.LENGTH_SHORT)
    }
    private fun uploadFile(){
        if(filePath != null){
            Toast.makeText(this,"Uploading", Toast.LENGTH_SHORT).show()
        }
        uploadFileName = UUID.randomUUID().toString()
        val imageRef = storageReference!!.child("locImages/" + uploadFileName)
        imageRef.putFile(filePath!!)
            .addOnSuccessListener {
                Toast.makeText(this,"File success to uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this,"File failed to upload", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    companion object {
        //IMAGE PICK CODE
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE ->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }else{
                    //permission from popup denied
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //handle image pick result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Picasso.get().load(data?.data).into(imageButtonLocation)
            filePath = data?.data
        }
    }
}