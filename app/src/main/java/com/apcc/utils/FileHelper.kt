package com.apcc.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.text.format.DateUtils
import com.apcc.data.FileApp
import com.apcc.emma.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream


object FileHelper {

    const val MAX_SIZE = 1024 * 1024 * 20 // maximum is 20MB

    fun getRootFolder():String{
        return Environment.getExternalStorageDirectory().toString()
    }

    /**
     * B / KB / MB / GB / TB
     */
    fun getFileSizeDisplace(fileApp: FileApp):String{
        return getFileSizeDisplace(fileApp.fileSize)
    }

    fun getFileSizeDisplace(fileSize: Double):String{
        return if (fileSize >= 1024.0 * 1024.0 * 1024.0 * 512.0) { // B / KB / MB / GB / TB >= 0.5TB
            "${DataHelper.numberAsText(fileSize / (1024.0 * 1024.0 * 1024.0 * 512.0))}TB"
        }else if (fileSize >= 1024.0 * 1024.0 * 512.0) { // B / KB / MB / GB >= 0.5GB
            "${DataHelper.numberAsText(fileSize / (1024.0 * 1024.0 * 512.0))}GB"
        }else if (fileSize >= 1024.0 * 512.0) { // B / KB / MB >= 0.5MB
            "${DataHelper.numberAsText(fileSize / (1024.0 * 512.0))}MB"
        }else if (fileSize >= 512.0) { // B / KB >= 0.5KB
            "${DataHelper.numberAsText(fileSize / 512.0)}KB"
        }else{ // B bytes
            "${DataHelper.numberAsText(fileSize)}B"
        }
    }

    fun validSize(file: File?): Boolean {
        file?.let {
            if (!it.exists()
                || !it.isFile
                || it.length() <= 0
                || it.length() > MAX_SIZE
            )
                return false
            return true
        }
        return false
    }

    /**
     * @cacheName: name of cache file with extension
     */
    suspend fun cacheFile(context: Context, inp: Bitmap, listener: Listener) {
        val fileApp = FileApp(DataHelper.generateID())
        fileApp.fileType = FileType.IMAGE

        val fileName = generateFilename(fileApp.fileAppID)

        val file = File(context.cacheDir, fileName)
        withContext(Dispatchers.IO) {
            var fOut:FileOutputStream? = null
            try {
                fOut = FileOutputStream(file)
                inp.compress(Bitmap.CompressFormat.JPEG, 100, fOut)

                fileApp.fileExtension = file.extension
                fileApp.path = file.absolutePath
                fileApp.fileSize = 1.0 * file.length()
                listener.onResult(fileApp, file, null)
            } catch (e: Exception) {
                e.printStackTrace()
                listener.onResult(null, null, context.getString(R.string.msg_fileCannotSave))
            } finally {
                fOut?.close()
            }
        }
    }

    fun getFileTypeViaExtension(file: File): Int {
        if (file.exists()){
            if (file.isDirectory)
                return FileType.FOLDER
            return getFileTypeViaExtension(file.extension)
        }
        return FileType.UNKNOW
    }

    /**
     * using file extension to detect file type
     * {@link FileType}
     */
    fun getFileTypeViaExtension(extension: String): Int {
        return when (extension.toLowerCase()) {
            "png", "jpg", "jpeg" -> FileType.IMAGE
            "doc", "docx", "txt", "json" -> FileType.DOCUMENT
            "mp3", "wav", "wma" -> FileType.SOUND
            "mp4" -> FileType.VIDEO
            else -> FileType.UNKNOW
        }
    }

    suspend fun getFileApp(context: Context, uri: Uri, listener: Listener){
        val fileApp = FileApp(DataHelper.generateID())

        getFile(context, uri, fileApp.fileAppID, object :Listener{
            override fun onResult(file: File?, error: String?) {
                if (file == null || error != null){
                    listener.onResult(null, null, null)
                    return
                }
                fileApp.fileType = getFileTypeViaExtension(file.extension)
                fileApp.fileExtension = file.extension
                fileApp.path = file.absolutePath
                fileApp.fileSize = 1.0 * file.length()

                listener.onResult(fileApp, file, null)
            }
        })
    }

    /**
     * get file from uri
     * some of case: we cannot get file from uri => go to cache it first, then return file has cache
     */
    suspend fun getFile(context: Context, uri: Uri, fileName: String = "", listener: Listener) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.let {input->
                val file = File(context.cacheDir, generateFilename(fileName, getFileExtension(context.contentResolver.getType(uri))))
                input.copyTo(file.outputStream())
                var error: String? = null
                if (!validSize(file))
                    error = context.getString(R.string.invalid_fileTooBig)
                listener.onResult(file, error)
            }
        }
    }

    fun getFile(filePath:String?):File?{
        if (filePath != null && filePath.isNotEmpty())
            return File(filePath)
        else
            return null
    }

    /**
     * normaly using file id and file extension to make file
     */
    fun generateFilename( fileName:String, extension:String = "jpg") = "$fileName.$extension"


    /**
     * MIME type : abc/xyz
     */
    fun getFileExtension( extension:String ?):String{
        extension?.let {
            return extension.split('/')[1]
        }
        return ""
    }

    fun getCacheFilePath(context: Context, fileApp: FileApp):String{
        return "${context.cacheDir.absolutePath}/${generateFilename(fileApp.fileAppID, fileApp.fileExtension?:"")}"
    }

    fun removeFile(filePath:String){
        val file = File(filePath)
        file.deleteRecursively()
    }

    fun checkExistFile(path:String?):Boolean{
        if (path != null && path.isNotEmpty()){
            val file = File(path)
            if (file.isFile)
                return true
        }
        return false
    }

    fun scanAllFile(filePath: String):List<File>{
        return scanAllFile(File(filePath))
    }
    fun scanAllFile(file:File):List<File>{
        if (file.exists()){
            if (file.isDirectory){
                return file.listFiles()?.toList()?: arrayListOf()
            }
        }
        return arrayListOf()
    }

    fun scanAllFile(file:File, filterTye:IntArray):List<File>?{
        if (file.exists()){
            if (file.isDirectory){
                return file.listFiles()?.toList()?: arrayListOf()
            }
        }
        return arrayListOf()
    }

    /**
     * #FileType.IMAGE
     * #FileType.VIDEO
     * ..........
     */

    fun scanAllFileViaType(file: File, fileTypes: IntArray):List<File>{
        val fileList:MutableList<File> = ArrayList()
        if (file.exists()){
            if (file.isDirectory){
                fileList.addAll(filterAllFileViaType(file, fileTypes))

                val filter = FileFilter { fileTemp->
                    fileTemp.exists() && fileTemp.isDirectory
                }
                val folderList = file.listFiles(filter)?: arrayOf()
                for (folder in folderList){
                    fileList.addAll(scanAllFileViaType(folder, fileTypes))
                }
            }
        }

        return fileList
    }

    fun filterAllFileViaType(filePath: String, fileTypes: IntArray):List<File>{
        return filterAllFileViaType(File(filePath), fileTypes)
    }

    fun filterAllFileViaType(file: File, fileTypes: IntArray):List<File>{
        if (file.exists()){
            if (file.isDirectory){
                val filter = FileFilter { fileTemp->
                    DataHelper.contains(fileTypes, getFileTypeViaExtension(fileTemp.extension))
                }
                return file.listFiles(filter)?.toList()?: arrayListOf()
            }
        }

        return arrayListOf()
    }

    fun convertListFileToFileApps(files: List<File>):List<FileApp>{
        val fileApps:MutableList<FileApp> = ArrayList()
        for (file in files){
            fileApps.add(convertFileToFileApp(file))
        }
        return fileApps
    }

    fun convertFileToFileApp(file: File):FileApp{
        val fileApp = FileApp()
        val exif = getExifInterface(file)
        fileApp.path = file.path
        fileApp.fileExtension = if (file.isFile) file.extension else ""
        fileApp.fileType = getFileTypeViaExtension(file)
        fileApp.fileDescription = exif?.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION)?:file.name
        fileApp.fileSize = if (file.isFile) file.length()*1.0 else 0.0
        fileApp.modifiedDate = DateHelper.dateTimeToString(file.lastModified())
        fileApp.createdDate = fileApp.modifiedDate

        return fileApp
    }

    private fun getExifInterface(file: File):ExifInterface?{
        try {
            if (file.exists() && file.isFile)
                return ExifInterface(file.canonicalPath)
        }catch (ex:Exception){
            ex.printStackTrace()
        }
        return null
    }

    fun getParentFolder(currentFile:String, limitToFolder:String = getRootFolder()):String{
        if (currentFile == limitToFolder){
            return ""
        }
        return currentFile.substringBeforeLast("/")
    }

    /**
     * also using for folder, file
     */
    fun getFileName(currentFile:String):String{
        if (currentFile.contains("/"))
            return currentFile.substringAfterLast("/")
        return currentFile
    }

    ///////////////////////////
    interface Listener {
        fun onResult(fileApp: FileApp?, file: File?, error: String?){}
        fun onResult(file: File?, error: String?){}
    }

}