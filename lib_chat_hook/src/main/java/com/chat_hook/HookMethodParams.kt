package com.chat_hook

/**
 * Hook方法的参数类
 */
class HookMethodParams(
    /** 需要hook的方法来自于的class */
    val hookClass:Class<*>,
    /** 需要hook的方法名称 */
    val hookMethodName:String,
    /**
     * 方法的参数列表。表示方法的，就是需要匹配的方法参数
     */
    val paramType:Array<Any>?,
    /**
     * 参数类型和Hook方法被调用的时候的回调：
     * 其他为参数类型。用于区分方法。主要是为了区别重载方法
     * 最后一个参数为参数的回调:[HookMethodCall]
     *
     * 注:此参数至少必须有一个回调。否则无法正常使用
     */
    val callback:HookMethodCall
)