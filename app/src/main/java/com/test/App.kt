package com.test

import android.app.Application
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.chat_hook.HookMethodCall
import com.chat_hook.HookMethodCallParams
import com.chat_hook.HookMethodHelper
import com.chat_hook.HookMethodParams

class App : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    //将图片设置为灰色
    private fun hookImageView() {
        HookMethodHelper.addHookMethod(
            HookMethodParams(
                ImageView::class.java, "updateDrawable",
                arrayOf(Drawable::class.java), object : HookMethodCall {
                    override fun beforeHookedMethod(param: HookMethodCallParams?) {
                        //影响参数，达到改变流程
                        if (param?.getArges()?.isNotEmpty() == true &&
                            param.getArges()!![0] != null &&
                            param.getArges()!![0] is Drawable
                        ) {
                            //原始的
                            val oldDrawable: Drawable = param.getArges()!![0] as Drawable
                            val resource = resources
                            val newDrawable: Drawable =
                                resource.getDrawable(android.R.drawable.sym_def_app_icon)
                            //将原始的图片资源替换为指定的图片资源。测试hook资源是否生效
                            param.getArges()!![0] = withBlackeningDrawable(oldDrawable)
//                        param.getArges()!![0] = withBlackeningDrawable(newDrawable)
                        }
                    }

                    override fun afterHookedMethod(param: HookMethodCallParams?) {
                        //影响结果达到改变流程的作用
                    }

                    //黑化化处理
                    private fun withBlackeningDrawable(drawable: Drawable): Drawable {
                        val matrix = ColorMatrix()
                        matrix.setSaturation(0F)
                        val colorFilter = ColorMatrixColorFilter(matrix)
                        drawable.colorFilter = colorFilter //增加混合的着色过滤颜色
                        return drawable
                    }
                })
        )
    }
}