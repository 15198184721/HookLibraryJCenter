package com.chat_hook

import android.content.Context
import android.os.Build
import android.util.Log
import com.swift.sandhook.HookLog
import com.swift.sandhook.SandHook
import com.swift.sandhook.SandHookConfig
import com.swift.sandhook.annotation.HookMethod
import com.swift.sandhook.annotation.HookMethodBackup
import com.swift.sandhook.xposedcompat.XposedCompat
import com.swift.sandhook.xposedcompat.utils.DexLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method
import java.util.*


/**
 * hook方法帮助类,此类用于hook指定方法的时候帮助，为了减少第三方库的暴露率所以进行代理转调
 * 底层参考:![https://github.com/ganyao114/SandHook]
 */
object HookMethodHelper {

    const val TAG = "hook"
    private var isInit = false

    //未执行的一些任务，就是堆积的任务。这个是在未初始化之前请求执行的任务
    private val notInitTaskList:MutableList<()->Unit> = Collections.synchronizedList(mutableListOf())

    /**
     * 添加一个构造方法的Hook，此只对构造方法生效(XSposed方式hook)
     *
     * @param hookParams 此方法不需要方法名称。所以可沈略方法名称参数
     */
    fun addHookConstructorMethod(hookParams: HookMethodParams) {
        notInitTaskList.add {
            try {
                val hookCall = getXC_HookMethodCall(hookParams)
                if (hookParams.paramType == null || hookParams.paramType.isEmpty()) {
                    XposedHelpers.findAndHookConstructor(
                        hookParams.hookClass,
                        hookCall
                    )
                } else {
                    val paramsTypeAndCall = mutableListOf<Any>()
                    //加入参数
                    paramsTypeAndCall.addAll(hookParams.paramType)
                    //加入回调
                    paramsTypeAndCall.add(hookCall)
                    XposedHelpers.findAndHookConstructor(
                        hookParams.hookClass,
                        *paramsTypeAndCall.toTypedArray()
                    )
                }
            } catch (e: Exception) {
                print("添加Hook拦截出现异常:$e")
            } catch (err: Error) {
                print("添加Hook拦截出现错误:$err")
            }
        }
        runCacheTask()
    }

    /**
     * 添加一个普通方法的Hook(Xsposed方式hook)
     * @param hookParams HookMethodParams
     */
    @HookMethod
    fun addHookMethod(hookParams: HookMethodParams) {
        notInitTaskList.add {
            try {
                val hookCall = getXC_HookMethodCall(hookParams)
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
        runCacheTask()
    }

    /**
     * 通过注解的方式来处理。原始支持的hook方式
     * 例如如下:
     *   @HookClass(Activity.class)
     *   public class ActivityHooker {
     *      //保存原方法(必须是静态的)
     *      @HookMethodBackup("onCreate")
     *      @MethodParams(Bundle.class)
     *      static Method onCreateBackup;
     *
     *      //hook的方法执行的操作，第一个参数必须是this
     *      @HookMethod("onCreate")
     *      @MethodParams(Bundle.class)
     *      public static void onCreate(Activity thiz, Bundle bundle) {
     *          Log.e("ActivityHooker", "hooked onCreate success " + thiz);
     *          onCreateBackup(thiz, bundle);
     *      }
     *  }
     * @param hookClass 处理这些hook的class
     */
    fun addHookMethodClass(vararg hookClass: Class<*>) {
        addHookMethodClass(null, *hookClass)
    }

    /**
     * 通过注解的方式来处理。原始支持的hook方式
     * @param classLoader 类加载器
     * @param hookClass 处理hook的类的class
     */
    fun addHookMethodClass(classLoader: ClassLoader?, vararg hookClass: Class<*>) {
        notInitTaskList.add {
            try {
                SandHook.addHookClass(classLoader, *hookClass)
            } catch (e: Exception) {
                print("SandHook方式添加hook异常:$e")
            } catch (err: Error) {
                print("SandHook方式添加hook错误:$err")
            }
        }
        runCacheTask()
    }

    /**
     * 调用原方法，如果使用的是原始支持的[addHookMethodClass]方式添加。
     * 需要调用此方法来执行原始方法或者调用Method的invoke来执行原方法，否则原方法不会执行
     *
     * @param originMethod 原方法([HookMethodBackup]注解标识的字段)
     * @param thizObj 此方法属于那个对象
     * @param params 调用此方法的参数
     */
    fun callOriginByBackup(originMethod: Method?, thizObj: Any?, vararg params: Any?) {
        notInitTaskList.add {
            try {
                SandHook.callOriginByBackup(originMethod, thizObj, *params)
            } catch (e: Exception) {
                print("SandHook方式添加调用原方法异常:$e")
            } catch (err: Error) {
                print("SandHook方式调用原方法错误:$err")
            }
        }
        runCacheTask()
    }



    /**
     * 执行已经缓存的任务，这个是为了防止在未初始化之前调用了任务
     */
    @Synchronized
    private fun runCacheTask(){
        if(!isInit){
            return //还未初始化
        }
        if(notInitTaskList.isNotEmpty()){
            for (function in notInitTaskList) {
                function.invoke()
            }
            print("SandHook执行列表任务数量:${notInitTaskList.size}")
            notInitTaskList.clear()
        }
    }

    /**
     * 初始化操作,自动初始化。不需要外部手动调用了
     * @param context Context
     */
    @Synchronized
    internal fun init(context: Context) {
        if(isInit){
            return
        }
        if (Build.VERSION.SDK_INT == 29 && getPreviewSDKInt() > 0) {
            // Android R preview
            SandHookConfig.SDK_INT = 30
        }
        SandHook.disableVMInline()
        SandHook.tryDisableProfile(context.packageName)
        //不设置的话。这可能会崩溃
        SandHook.disableDex2oatInline(false)
        //取消对内联hook方法的调用程序的优化,需要 >= 7.0
//        SandHook.deCompileMethod()

        if (SandHookConfig.SDK_INT >= Build.VERSION_CODES.P) {
            SandHook.passApiCheck()
        }

        //setup for xposed
        XposedCompat.cacheDir = context.cacheDir
        XposedCompat.context = context
        XposedCompat.classLoader = context.classLoader
        XposedCompat.isFirstApplication = true

        //是否为debug模式。表示是否输出相关日志
        HookLog.DEBUG = false
        DexLog.DEBUG = false

        //初始化
        isInit = true
        runCacheTask()
    }

    /**
     * 打印日志
     * @param msg String
     */
    internal fun print(msg: String) {
        Log.v(TAG, msg)
    }

    /**
     * 获取参数Hook的回调参数
     * @param hookParams HookMethodParams
     * @return XC_MethodHook
     */
    private fun getXC_HookMethodCall(hookParams: HookMethodParams): XC_MethodHook {
        return if (hookParams.callback is HookMethodReplacemCall) {
            object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    return try {
                        hookParams.callback.replaceHookedMethod(HookMethodCallParams(param!!))
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            throw e //调试模式。抛出异常
                        }
                        null
                    }
                }
            }
        } else {
            object : XC_MethodHook(PRIORITY_HIGHEST) {
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
        }
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