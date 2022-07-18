package com.example.test0

import com.google.gson.JsonArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ClassPub (val birth: String, val name: String, val relations: String, val sex: String, val ssn: String, val user_id: String, val id: String, val address: String ) {
}
//class subClassPub (val public_data: )
//class subsubClassPub (val public_data: )

//class GetList{
//    @SerializedName("user_id")
//    @Expose
//    val id: String = ""
//
//}
//data class PubResponse(
//    @SerializedName("results")
//    val results: List<ClassPub>
//)