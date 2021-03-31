package com.chat_hook

/**
 * 当hook发生的时候的回调监听
 */
interface HookMethodCall {
    /**
     * hook执行之前的回调
     */
    fun beforeHookedMethod(param: HookMethodCallParams?){}

    /**
     * hook执行之后的回调
     */
    fun afterHookedMethod(param: HookMethodCallParams?)
}