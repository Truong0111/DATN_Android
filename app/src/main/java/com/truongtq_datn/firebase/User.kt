package com.truongtq_datn.firebase

class User(
    var idAccount: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var refId: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var arrDoor: MutableList<String> = mutableListOf(),
    var role: MutableList<String> = mutableListOf("user")
) {

    fun getFullName(): String {
        return "$firstName $lastName"
    }

    override fun toString(): String {
        return "User(idAccount='$idAccount', firstName='$firstName', lastName='$lastName', refId='$refId', " +
                "email='$email', phoneNumber='$phoneNumber', password='[PROTECTED]', " +
                "arrDoor=$arrDoor, role=$role)"
    }
}