package com.chat_hook

/**
 * 当hook发生的时候的回调监听,此监听和HookMethodCall完全一致。唯一的区别是：
 *  HookMethodCall：此回调依然会执行原方法。只是可以修改参数、返回对流程进行干预
 *  HookMethodReplacemCall：此回调会完全信任结果。并且不再继续执行原始方法
 */
abstract class HookMethodReplacemCall : HookMethodCall {
    /** @hide */
    final override fun beforeHookedMethod(param: HookMethodCallParams?) {}

    /** @hide */
    final override fun afterHookedMethod(param: HookMethodCallParams?) {}


    /**
     * 此方法就是替换掉原方法需要执行的逻辑，并且会将此方法返回参数作为实际的最终依据。完全信任。
     * 并且不再继续执行原hook方法
     * @param param
     */
    abstract fun replaceHookedMethod(param: HookMethodCallParams?): Any?

}