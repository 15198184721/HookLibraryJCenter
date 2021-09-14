## Version

[ ![Version](https://api.bintray.com/packages/15198184721/android/SandHook/images/download.svg) ](https://bintray.com/15198184721/android/SandHook/_latestVersion)

##版本更新说明

##使用方法

```gradle
//1.0.1 尽量需替换为最新版(jcenter，目前已废弃)
implementation 'cn.lk.hook:lib_chat_hook:1.0.1'
//1.0 已提交maven
implementation 'cn.lk.hook:lib_chat_hook:1.0'
```

## HookLibraryJCenter

上传到jcenter的一个hook库,主要是对第三方的库转调部分功能。包装了部分功能,目前只是做了方法的hook转调
使用方法:
```kotlin
    @Deprecated("已经被废弃,此初始化不在是必须的操作。已经在其他地方自动初始化")
    HookMethodHelper.init() //初始化(已经废弃。可以不初始化,会自动初始化)

    HookMethodHelper.addHookMethod() //添加方法的hook指定方法
    HookMethodHelper.addHookConstructorMethod() //hook构造方法
    HookMethodHelper.addHookMethodClass() //通过原始方式进行hook添加
```
###使用方法如下：

```kotlin
//添加一个对 TestHook.testMethod 的hook  ,HookMethodCall作为监听表示要调用原方法
HookMethodHelper.addHookMethod(
                HookMethodParams(TestHook::class.java, "testMethod",
                    null, object : HookMethodCall {
                        override fun beforeHookedMethod(param: HookMethodCallParams?) {
                            super.beforeHookedMethod(param)
                        }
    
                        override fun afterHookedMethod(param: HookMethodCallParams?) {
                            Log.e("this", "调用了指定的hook方法 ---> testMethod")
                        }
                    })
            )

//添加一个对 TestHook.testMethod 的hook，HookMethodReplacemCall作为回调。表示不调用原始方法
HookMethodHelper.addHookMethod(
                HookMethodParams(TestHook::class.java, "testMethod",
                    null, object : HookMethodReplacemCall() {
                        override fun replaceHookedMethod(param: HookMethodCallParams?): Any? {
                            Log.e("this", "调用了指定的hook方法 ---> testMethod")
                            return -369
                        }
                    })
            )
//给TestHook的构造方法添加一个hook
HookMethodHelper.addHookConstructorMethod(
                HookMethodParams(TestHook::class.java, null,
                    null, object : HookMethodCall {
                        override fun afterHookedMethod(param: HookMethodCallParams?) {
                            Log.e("this", "调用了构造方法 ---> TestHook()")
                        }
                    })
            )

//或者使用原始方式
HookMethodHelper.addHookMethodClass(ActivityHookder::class.java)
```

###ActivityHookder

```kotlin
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
            Log.e("this", "hooked testHook success " + thiz);
            //执行原方法
//            onResumeBackup?.invoke(thiz)
            //或者
            HookMethodHelper.callOriginByBackup(onResumeBackup,thiz)
        }
    }
}
```

##说明：
此库基于[SandHook](https://github.com/ganyao114/SandHook)
但为了出现兼容问题好处理进行了部分暴露处理，让后期出现兼容问题能够及时抽离。只是自己内部使用

