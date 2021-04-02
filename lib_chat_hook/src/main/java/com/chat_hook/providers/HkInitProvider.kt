package com.chat_hook.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.chat_hook.HookMethodHelper

/**
 * 本框架的自动初始化内容提供者。主要是实现自动初始化
 */
class HkInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val context: Context = context ?: return false
        Log.v(context.packageName,"------- start init hook library ------")
        //调用hook的初始化相关操作
        HookMethodHelper.init(context)
        Log.v(context.packageName,"------- init hook library finish------")
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}