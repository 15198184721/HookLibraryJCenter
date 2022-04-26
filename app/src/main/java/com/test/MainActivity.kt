package com.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.chat_hook.*
import com.lk.hook.R

class MainActivity : AppCompatActivity() {

    val iv:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //原始方式hook
//        HookMethodHelper.addHookMethodClass(ActivityHookder::class.java)
        //Xspcoe方式
        //以修改参数方式进行。此方法无论如何都会调用原方法。但是可以通过修改参数和结果来干预其运行流程
//        HookMethodHelper.addHookMethod(
//            HookMethodParams(TestHook::class.java, "testMethod",
//                null, object : HookMethodCall {
//                    override fun beforeHookedMethod(param: HookMethodCallParams?) {
//                        super.beforeHookedMethod(param)
//                    }
//
//                    override fun afterHookedMethod(param: HookMethodCallParams?) {
//                        Log.e("this", "调用了指定的hook方法 ---> testMethod")
//                    }
//                })
//        )
        //以为不调用原方法的方式进行hook
        HookMethodHelper.addHookMethod(
            HookMethodParams(TestHook::class.java, "testMethod",
                null, object : HookMethodReplacemCall() {
                    override fun replaceHookedMethod(param: HookMethodCallParams?): Any? {
                        Log.e("this", "调用了指定的hook方法 ---> testMethod")
                        return -369
                    }
                })
        )
        //hook构造方法
//        HookMethodHelper.addHookConstructorMethod(
//            HookMethodParams(TestHook::class.java, null,
//                null, object : HookMethodCall {
//                    override fun afterHookedMethod(param: HookMethodCallParams?) {
//                        Log.e("this", "调用了构造方法 ---> TestHook()")
//                    }
//                })
//        )
        findViewById<View>(R.id.test_hook)
            .setOnClickListener {
                Log.e("this", "调用了testMethod 结果 = ${TestHook().testMethod()}")
            }
        testHook()
    }

    override fun onResume() {
        super.onResume()
        testHook()
    }

    fun testHook() {
        Log.e("this", "执行方法。ScandHook方式的hook。这是目标方法 ---> testHook()")
    }

}