package com.apcc.emma.ui.file

import com.apcc.data.FileApp

interface FileCallback {
    fun transFileDetail(className: String, fileApp: FileApp)
    fun transVideoPlayer(className: String, fileApp: List<FileApp>)
    fun transMusicPlayer(className: String, fileApp: List<FileApp>)

    fun transFileLocal(className: String)

    /**
     * from file list, take and return
     * take and get it
     */
    fun transCamera(className: String)
    fun popBack(className: String)
    fun returnResultFile(fileApp: FileApp?)
    fun returnResultFile(fileApps: ArrayList<FileApp>)
}