package com.test.hooker

import android.util.Log
import com.chat_hook.HookMethodHelper
import com.swift.sandhook.annotation.HookClass
import com.swift.sandhook.annotation.HookMethod
import com.swift.sandhook.annotation.HookMethodBackup
import com.swift.sandhook.annotation.ThisObject
import com.test.MainActivity
import java.lang.reflect.Method

@HookClass(MainActivity::class)
class ActivityHookder {

    companion object {
        @JvmStatic
        @HookMethodBackup("testHook")
        private var onResumeBackup: Method? = null

        @HookMethod("testHook")
        //@MethodParams(Bundle.class) 如果有参数。使用此注解告知参数类型。方法名称和参数共同确定一个方法
        @JvmStatic //hook的回调必须是静态的
        private fun hook_testHook(@ThisObject thiz: MainActivity) {
            Log.e("this", "你说调用的方法已经被我给拦截了。心情好我就给你放过去了。感谢我吧.. " + thiz);
            //执行原方法
//            onResumeBackup?.invoke(thiz)
            //或者
            HookMethodHelper.callOriginByBackup(onResumeBackup,thiz)
        }
    }
}