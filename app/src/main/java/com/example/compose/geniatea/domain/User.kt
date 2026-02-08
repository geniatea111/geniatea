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
    var showPictograms: Boolean? = null
    var onboardingCompleted: Boolean = false
    var isLoggedIn: Boolean = false

    constructor(id: Long, token: String, refreshToken: String, name: String, email: String, username: String, birthdate: String , gender: String, rol : String, showPictograms: Boolean? = null, onboardingCompleted: Boolean = false) {
        this.id = id
        this.accessToken = token
        this.refreshToken = refreshToken
        this.name = name
        this.email = email
        this.username = username
        this.birthdate = birthdate
        this.gender = gender
        this.rol = rol
        this.showPictograms = showPictograms
        this.onboardingCompleted = onboardingCompleted
    }

    constructor(id: Long, accessToken: String, refreshToken: String, name: String, onboardingCompleted: Boolean = false) {
        this.id = id
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.name = name
        this.onboardingCompleted = onboardingCompleted
    }

    constructor(name: String, email: String, username: String, birthdate: String , gender: String, showPictograms: Boolean? = null) {
        this.name = name
        this.email = email
        this.username = username
        this.birthdate = birthdate
        this.gender = gender
        this.showPictograms = showPictograms
    }

    constructor(email : String) {
        this.email = email
    }

    fun copy(
        id: Long = this.id,
        accessToken: String = this.accessToken,
        refreshToken: String = this.refreshToken,
        name: String = this.name,
        email: String = this.email,
        username: String = this.username,
        birthdate: String = this.birthdate,
        gender: String = this.gender,
        rol: String = this.rol,
        showPictograms: Boolean? = this.showPictograms,
        onboardingCompleted: Boolean = this.onboardingCompleted,
        isLoggedIn: Boolean = this.isLoggedIn
    ): User {
        val newUser = User(id, accessToken, refreshToken, name, email, username, birthdate, gender, rol, showPictograms, onboardingCompleted)
        newUser.isLoggedIn = isLoggedIn
        return newUser
    }
}