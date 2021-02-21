package com.yogeshpaliyal.keypass.utils

import java.security.SecureRandom
import java.util.*


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 20-02-2021 22:10
*/

fun getRandomString(sizeOfRandomString: Int): String {
    val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM!@#$%&*_+"
    val random = SecureRandom()
    val sb = StringBuilder(sizeOfRandomString)
    for (i in 0 until sizeOfRandomString) sb.append(
        ALLOWED_CHARACTERS[random.nextInt(
            ALLOWED_CHARACTERS.length
        )]
    )
    return sb.toString()
}
