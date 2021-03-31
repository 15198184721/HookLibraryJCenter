package com.chat_hook

/**
 * Hook方法的参数类
 */
class HookMethodParams(
    /** 需要hook的方法来自于的class */
    val hookClass:Class<*>,
    /** 需要hook的方法名称 */
    val hookMethodName:String,
    /** 当Hook方法被调用的时候的回调 */
    val hookCallBack: HookMethodCall
)