package com.mikaelzero.kounk

import java.awt.Color
import java.lang.StringBuilder
import java.util.*


/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/10/18 3:29 下午
 * @Description:
 */
class ResTemplate {
    companion object {
        const val DRAWABLE = """<?xml version="1.0" encoding="utf-8"?>
            <vector xmlns:android="http://schemas.android.com/apk/res/android"
                android:width="108dp"
                android:height="108dp"
                android:viewportWidth="108"
                android:viewportHeight="108">
                <path
                    android:fillColor="#3DDC84"
                    android:pathData="M0,0h108v108h-108z" />
                <path
                    android:fillColor="#00000000"
                    android:pathData="M9,0L9,108"
                    android:strokeWidth="0.8"
                    android:strokeColor="#33FFFFFF" />"""
        const val DRAWABLE_END = "  </vector>"

        const val STRING_NODE = "<string name=\"\${stringName}\">\${stringValue}</string>"

        const val TEMPLATE = "<resources>\n" +
                "</resources>"

        fun getLayoutTemplate(): String {
            return """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center"
    android:gravity="center"
    android:orientation="vertical">

    <TextView
        android:id="@+id/textview_first"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="${getRandomString()}" />

    <Button
        android:id="@+id/button_first"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="${getRandomString()}" />
</LinearLayout>
        """
        }

        fun getDrawablePath(): String {
            val sp = StringBuilder()
            for (x in 0 until Random().nextInt(10)) {
                sp.append(getDrawablePathSingle())
            }
            return sp.toString()
        }

        fun getDrawablePathSingle(): String {
            val mRandom = Random().nextInt(6).toString() + "9"
            val mRandom2 = Random().nextInt(6).toString() + "9"
            val mRandom3 = Random().nextInt(6).toString() + "9"
            val randomStrokeWidth = Random().nextInt(10).toString()
            val randomColor = String.format("#%06x", Random().nextInt(0xffffff + 1))
            return """<path
        android:fillColor="#00000000"
        android:pathData="M${mRandom},0L${mRandom2},${mRandom3}"
        android:strokeWidth="$randomStrokeWidth"
        android:strokeColor="$randomColor" /> """
        }
    }


}