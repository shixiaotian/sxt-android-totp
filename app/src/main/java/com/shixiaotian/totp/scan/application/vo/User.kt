package com.shixiaotian.totp.scan.application.vo

class User {

    private var id : Int = 0
    private var username : String=""
    private var secretKey : String=""
    private var issuer : String=""
    private var code : String=""

    constructor(id: Int, username: String, secretKey: String, issuer: String) {
        this.id = id
        this.username = username
        this.secretKey = secretKey
        this.issuer = issuer
    }

    fun getId() : Int {
        return id
    }
    fun setId(id : Int) {
        this.id = id
    }

    fun getUsername() : String {
        return username
    }
    fun setUsername(username : String) {
        this.username = username
    }

    fun getSecretKey() : String {
        return secretKey
    }
    fun setSecretKey(secretKey : String) {
        this.secretKey = secretKey
    }

    fun getCode() : String {
        return code
    }
    fun setCode(code : String) {
        this.code = code
    }

    fun getIssuer() : String {
        return issuer
    }
    fun setIssuer(issuer : String) {
        this.issuer = issuer
    }
}