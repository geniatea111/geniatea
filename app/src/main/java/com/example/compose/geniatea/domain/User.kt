package com.example.compose.geniatea.domain

class User {
    var id: Long = 0L
    var accessToken : String = ""
    var refreshToken: String = ""
    var name: String = ""
    var email: String = ""
    var username : String = ""
    var birthdate : String = ""
    var gender : String = ""
    var rol = "USER" // Default role is user, can be changed later
    var isLoggedIn: Boolean = false

    constructor(id: Long, token: String, refreshToken: String, name: String, email: String, username: String, birthdate: String , gender: String, rol : String) {
        this.id = id
        this.accessToken = token
        this.refreshToken = refreshToken
        this.name = name
        this.email = email
        this.username = username
        this.birthdate = birthdate
        this.gender = gender
        this.rol = rol

    }

    constructor(id: Long, accessToken: String, refreshToken: String, name: String) {
        this.id = id
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.name = name
    }

    constructor(name: String, email: String, username: String, birthdate: String , gender: String) {
        this.name = name
        this.email = email
        this.username = username
        this.birthdate = birthdate
        this.gender = gender
    }

    constructor(email : String) {
        this.email = email
    }
}