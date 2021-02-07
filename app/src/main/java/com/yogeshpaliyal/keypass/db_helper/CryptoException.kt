package com.yogeshpaliyal.keypass.db_helper


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 07-02-2021 18:52
*/

class CryptoException : Exception {
    constructor() {}
    constructor(message: String?, throwable: Throwable?) : super(message, throwable) {}
}
