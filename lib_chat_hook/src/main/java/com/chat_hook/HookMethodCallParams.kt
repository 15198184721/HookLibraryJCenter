package com.chat_hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 转调的参数，就是当hook发生时候。回调的参数
 * 注:
 * 这种方式没有办法实现不调用原方法,只能做到对结果和参数进行干预
 */
class HookMethodCallParams(
    /** 依赖的原始参数，此处是实际的参数 */
    internal val oldParams: XC_MethodHook.MethodHookParam
) {
    /** Returns the result of the method call.  */
    fun getResult(): Any? {
        return oldParams.result
    }

    /**
     * Modify the result of the method call.
     *
     *
     * If called from [.beforeHookedMethod], it prevents the call to the original method.
     */
    fun setResult(result: Any?) {
        oldParams.result = result
    }

    /** Returns the [Throwable] thrown by the method, or `null`.  */
    fun getThrowable(): Throwable? {
        oldParams.method =
            return oldParams.throwable
    }

    /** Returns true if an exception was thrown by the method.  */
    fun hasThrowable(): Boolean {
        return oldParams.throwable != null
    }

    /**
     * Modify the exception thrown of the method call.
     *
     *
     * If called from [.beforeHookedMethod], it prevents the call to the original method.
     */
    fun setThrowable(throwable: Throwable) {
        oldParams.throwable = throwable
    }

    /** Returns the result of the method call, or throws the Throwable caused by it.  */
    @Throws(Throwable::class)
    fun getResultOrThrowable(): Any? {
        return oldParams.resultOrThrowable
    }

    fun getMethod(): Member {
        return oldParams.method
    }

    fun getThisObject(): Any? {
        return oldParams.thisObject
    }

    fun getArges(): Array<Any?>? {
        return oldParams.args
    }

    /**
     * 是否已经返回内容
     * @return T:已返回，F:未返回
     */
    fun getReturnEarly(): Boolean {
        return try {
            val fieldName = "oldParams"
            XposedHelpers.getBooleanField(oldParams, fieldName)
        } catch (e: Exception) {
            false
        } catch (err: Error) {
            false
        }
    }
}