package com.gj.arcoredraw

import com.blankj.utilcode.util.ToastUtils
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.sceneform.ux.ArFragment

//可以直接使用ArFragment   我这里为了中文提示
class MyArFragment : ArFragment() {
    override fun handleSessionException(sessionException: UnavailableException) {
        val message: String
        when (sessionException) {
            is UnavailableArcoreNotInstalledException -> message = "请安装ARCore"
            is UnavailableApkTooOldException -> message = "请升级ARCore"
            is UnavailableSdkTooOldException -> message = "请升级app"
            is UnavailableDeviceNotCompatibleException -> message = "当前设备部不支持AR"
            else -> {
                message = "未能创建AR会话,请查看机型适配,arcore版本与系统版本"
                val var3 = sessionException.toString()
            }
        }
        ToastUtils.showLong(message)
    }
}