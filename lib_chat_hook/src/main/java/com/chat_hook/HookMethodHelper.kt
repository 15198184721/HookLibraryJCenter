package com.chat_hook

import android.content.Context
import android.os.Build
import android.util.Log
import com.swift.sandhook.SandHook
import com.swift.sandhook.SandHookConfig
import com.swift.sandhook.xposedcompat.XposedCompat
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * hook方法帮助类,此类用于hook指定方法的时候帮助，为了减少第三方库的暴露率所以进行代理转调
 * 底层参考:![https://github.com/ganyao114/SandHook]
 */
object HookMethodHelper {

    const val TAG = "hook"

    /**
     * 初始化操作
     * @param context Context
     */
    fun init(context: Context) {
        if (Build.VERSION.SDK_INT == 29 && getPreviewSDKInt() > 0) {
            // Android R preview
            SandHookConfig.SDK_INT = 30
        }
        SandHook.disableVMInline()
        SandHook.tryDisableProfile(context.packageName)
        //不设置的话。这可能会崩溃
        SandHook.disableDex2oatInline(false)

        if (SandHookConfig.SDK_INT >= Build.VERSION_CODES.P) {
            SandHook.passApiCheck()
        }

        //通过注解的方式hook
//        try {
//            SandHook.addHookClass(
//                JniHooker::class.java,
//                CtrHook::class.java,
//                LogHooker::class.java,
//                CustmizeHooker::class.java,
//                ActivityHooker::class.java,
//                ObjectHooker::class.java,
//                NewAnnotationApiHooker::class.java
//            )
//        } catch (e: HookErrorException) {
//            e.printStackTrace()
//        }

        //setup for xposed
        XposedCompat.cacheDir = context.cacheDir
        XposedCompat.context = context
        XposedCompat.classLoader = context.classLoader
        XposedCompat.isFirstApplication = true
    }

    /**
     * 添加一个hook到本地
     * @param hookParams HookMethodParams
     */
    fun addHookMethod(hookParams: HookMethodParams) {
        try {
            val hookCall = object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    try {
                        hookParams.callback.beforeHookedMethod(
                            HookMethodCallParams(param)
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e //调试模式。抛出异常
                        }
                    }
                }

                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    try {
                        hookParams.callback.afterHookedMethod(
                            HookMethodCallParams(param)
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e //调试模式。抛出异常
                        }
                    }
                }
            }
            if (hookParams.paramType == null || hookParams.paramType.isEmpty()) {
                XposedHelpers.findAndHookMethod(
                    hookParams.hookClass,
                    hookParams.hookMethodName,
                    hookCall
                )
            } else {
                val paramsTypeAndCall = mutableListOf<Any>()
                //加入参数
                paramsTypeAndCall.addAll(hookParams.paramType)
                //加入回调
                paramsTypeAndCall.add(hookCall)
                XposedHelpers.findAndHookMethod(
                    hookParams.hookClass,
                    hookParams.hookMethodName,
                    *paramsTypeAndCall.toTypedArray()
                )
            }
        } catch (e: Exception) {
            print("添加Hook拦截出现异常:$e")
        } catch (err: Error) {
            print("添加Hook拦截出现错误:$err")
        }
    }

    /**
     * 打印日志
     * @param msg String
     */
    internal fun print(msg: String) {
        Log.e(TAG, msg)
    }

    private fun getPreviewSDKInt(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                return Build.VERSION.PREVIEW_SDK_INT
            } catch (e: Throwable) {
                // ignore
            }
        }
        return 0
    }
}