package com.yogeshpaliyal.keypass.utils

import android.content.Context
import androidx.annotation.StringDef
import androidx.core.content.edit
import com.yogeshpaliyal.keypass.MyApplication
import com.yogeshpaliyal.keypass.R
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 22-01-2021 22:40
*/

object SharedPrefHelper {

    @StringDef(open = false,value = [
        SharedPrefKeys.USER_PIN
    ])
    annotation class SharedPrefKeys{
        companion object{
            const val USER_PIN = "USER_PIN"
        }
    }




    private val sp by lazy {
        MyApplication.instance.getSharedPreferences(
            MyApplication.instance.getString(R.string.app_name),
            Context.MODE_PRIVATE
        );
    }


    fun setString(@SharedPrefKeys key: String,value : String?){
        sp.edit { putString(key,value)}
    }

    fun getString(@SharedPrefKeys key: String, default : String = "") = sp.getString(key,default)

}