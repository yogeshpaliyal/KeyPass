package com.yogeshpaliyal.keypass.constants

annotation class AccountType(){
    companion object{
        const val DEFAULT = 1 // used to store password and user information
        const val TOPT = 2 // used to store Time base - One time Password
    }
}
