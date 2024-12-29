package com.shixiaotian.totp.scan.application.tools

import android.icu.util.Calendar

class MyTimeUtils {

    companion object {
        @JvmStatic fun getCurrentSec(): Long {
            // 获取当前时间的毫秒数
            val currentTimeMillis = System.currentTimeMillis()

            // 创建Calendar实例
            val calendar = Calendar.getInstance()

            // 设置Calendar的时间为当前时间
            calendar.timeInMillis = currentTimeMillis

            // 将秒和毫秒字段重置为0
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // 当前分钟的开始时间的毫秒数
            val startOfCurrentMinuteMillis = calendar.timeInMillis

            // 已过去的毫秒数
            val elapsedMillis = currentTimeMillis - startOfCurrentMinuteMillis

            // 已过去的秒数
            //val elapsedSeconds = elapsedMillis / 1000

            if(elapsedMillis > 30000) {
                return 60000 - elapsedMillis;
            } else {
                return 30000 - elapsedMillis;
            }

        }
    }
}