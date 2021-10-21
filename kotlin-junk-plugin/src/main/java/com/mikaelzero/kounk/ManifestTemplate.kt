package com.mikaelzero.kounk

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/10/18 4:03 下午
 * @Description:
 */
class ManifestTemplate {
    companion object {
        const val TEMPLATE = "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">\n" +
                "    <application>\n" +
                "    </application>\n" +
                "</manifest>"
        const val ACTIVITY_NODE = "<activity android:name=\"\${packageName}.\${activityName}\"/>"
    }
}