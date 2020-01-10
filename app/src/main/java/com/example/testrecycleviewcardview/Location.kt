package com.example.testrecycleviewcardview

import com.google.firebase.database.Exclude

class Location{
    var locationName:String? = null
    var locationAddress:String? = null
    var locationImage:String? = null
    var locationOperation:String? = null


    constructor(){}

    constructor(
        locationName: String?,
        locationAddress: String?,
        locationImage: String?,
        operation: String?
    ) {
        this.locationName = locationName
        this.locationAddress = locationAddress
        this.locationImage = locationImage
        this.locationOperation = operation
    }

    constructor(locationName: String?, locationAddress: String?, locationOperation: String?) {
        this.locationName = locationName
        this.locationAddress = locationAddress
        this.locationOperation = locationOperation
    }

    @Exclude
    fun toMap():Map<String,Any>{
        val result = HashMap<String, Any>()
        result.put("locationName",locationName!!)
        result.put("locationAddress",locationAddress!!)
        result.put("locationImage",locationImage!!)
        result.put("locationOperation",locationOperation!!)

        return result
    }
}