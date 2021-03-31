package com.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams
import com.lk.hook.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HookMethodHelper.addHookMethod(
            HookMethodParams(TestHook::class.java, "testMethod",
                arrayOf(String::class.java, Int::class.java), object : HookMethodCall {
                    override fun afterHookedMethod(param: HookMethodCallParams?) {
                        Log.e("this", "调用了指定的hook方法 ---> testMethod")
                    }
                })
        )
        findViewById<View>(R.id.test_hook)
            .setOnClickListener {
                TestHook().testMethod("")
            }
    }
}