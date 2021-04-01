package com.test

import android.util.Log

class TestHook {
    fun testMethod():Int{
        Log.e("this", "执行原方法 ---> testMethod-参数0")
        return 123
    }
    fun testMethod(s:String){
        Log.e("this", "执行原方法 ---> testMethod-参数1")
    }
    fun testMethod(s:String,i:Int){
        Log.e("this", "执行原方法 ---> testMethod-参数2")
    }
    fun testMethod(s:String,i:Int,s1:String){
        Log.e("this", "执行原方法 ---> testMethod-参数3")
    }
    fun testMethod(s:String,i:Int,s1:String,o:Any){
        Log.e("this", "执行原方法 ---> testMethod-参数4")
    }
}