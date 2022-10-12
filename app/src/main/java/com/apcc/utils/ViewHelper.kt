package com.apcc.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.apcc.emma.R
import com.apcc.framework.AppManager
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import java.io.File

object ViewHelper {

    fun updateMenuTint(menu: Menu){
        if (menu.size() > 0){
            for ( i in 0 until menu.size()){
                updateMenuTint(menu.getItem(i))
            }
        }
    }

    /**
     * support for menu drawable icon
     * working for API sdk < 26
     * from 26, it working auto from xml
     */
    fun updateMenuTint(menuItem: MenuItem?){
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            menuItem?.let { mn->
                mn.icon?.let { ic->
                    ic.setTintList(
                        ContextCompat.getColorStateList(
                            AppManager.instance,
                            R.color.menu_tint
                        )
                    )
                    mn.icon = ic
                }
            }
//        }
    }

    fun loadImage(imgPath: String?, defaultImg: Int = 0, allowCache: Boolean = false): RequestCreator? {
        imgPath?.let { path->
            val request: RequestCreator
            if(FileHelper.checkExistFile(path)){
                // is local file
                request = Picasso.get().load(File(path))
            }else if (DataHelper.isValidUrl(path)){
                // is online file
                request  = Picasso.get().load(path)
            }else{
                // make online path as normal
                request = Picasso.get().load(DataHelper.formatFileUrl(path))
            }

            if(defaultImg > 0) request.error(defaultImg)
            if (allowCache) request.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            return request
        }
        return null
    }

    fun getViewBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }
}