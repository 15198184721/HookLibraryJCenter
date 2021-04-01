package com.chat_hook

/**
 * Hook方法的参数类
 */
class HookMethodParams(
    /** 需要hook的方法来自于的class */
    val hookClass:Class<*>,
    /** 需要hook的方法名称 */
    val hookMethodName:String?,
    /**
     * 方法的参数列表。表示方法的，就是需要匹配的方法参数(名称和参数唯一匹配才会hook)
     * 为空或者长度为0都表示没有参数的方法
     */
    val paramType:Array<Any>?,
    /**
     * 参数类型和Hook方法被调用的时候的回调：
     */
    val callback:HookMethodCall
)