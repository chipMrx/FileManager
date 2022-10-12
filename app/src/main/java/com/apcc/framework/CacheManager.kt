package com.apcc.framework

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.apcc.repository.response.CommonResponse
import com.apcc.utils.LayoutType


object CacheManager {
    private const val CACHE_NAME = "anPhatCSCache"
    // config
    private const val KEY_HAS_CONFIG = "keyHasConfig"
    private const val KEY_URL_PATH_FILE = "keyUrlPathFile"
    private const val KEY_CARD_SERVICE = "keyCardService"
    // info
    private const val KEY_SESSION = "keySession"
    private const val KEY_USER_ID = "keyUserID"
    private const val KEY_LOGIN_ACC = "keyLoginAcc"
    private const val KEY_LOGIN_MAIL = "keyLoginMail"
    private const val KEY_LOGIN_PASS = "keyLoginPass"
    private const val KEY_VIEW_LAYOUT_TYPE = "keyViewLayoutType"
    // sync flag
    const val KEY_REQUEST_SYNC = "keyRequestSync"
    // camera setting
    private const val KEY_CAMERA_TIME_STAMP = "keyCameraTimeStamp"
    private const val KEY_CAMERA_FRAME = "keyCameraFrame"
    private const val KEY_CAMERA_QUALITY = "keyCameraQuality"
    private const val KEY_CAMERA_TIMER = "keyCameraTimer"
    private const val KEY_CAMERA_FLASH = "keyCameraFlash"


    private var mPrefs: SharedPreferences? = null

    private fun initCache(context: Context) {
        if (mPrefs == null) {
            mPrefs = context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE)
        }
    }

    fun listenerCacheChange(context: Context, listener:OnSharedPreferenceChangeListener){
        initCache(context)
        mPrefs?.registerOnSharedPreferenceChangeListener(listener)

    }


    //////////////////////////////////////////////////////////////////////////////
    /// config
    //////////////////////////////////////////////////////////////////////////////

    fun saveConfig(context: Context, commonResponse: CommonResponse): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_URL_PATH_FILE, commonResponse.cgfUrlPathFile)
            prefsEditor.putBoolean(KEY_CARD_SERVICE, commonResponse.cgfCardService == true)
            prefsEditor.putBoolean(KEY_HAS_CONFIG, true)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun isHasConfig(context: Context): Boolean {
        initCache(context)
        return mPrefs?.getBoolean(KEY_HAS_CONFIG, false)?:false
    }

    fun getUrlPathFile(context: Context): String {
        initCache(context)
        var urlPath:String? = null
        mPrefs?.let {
            urlPath = it.getString(KEY_URL_PATH_FILE, "")
        }
        return urlPath?:""
    }

    fun getCardService(context: Context): Boolean {
        initCache(context)
        mPrefs?.let {
            return it.getBoolean(KEY_CARD_SERVICE, false)
        }
        return false
    }
    //////////////////////////////////////////////////////////////////////////////
    /// user
    //////////////////////////////////////////////////////////////////////////////

    fun cleanSessionInfo(context: Context) {
        saveSessionInfo(context, "", "", "", "", "")
    }

    fun saveSessionInfo(
        context: Context,
        sessionID: String?,
        userID: String?,
        account: String?,
        email: String?,
        password: String?
    ): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_SESSION, sessionID)
            prefsEditor.putString(KEY_USER_ID, userID)
            prefsEditor.putString(KEY_LOGIN_ACC, account)
            prefsEditor.putString(KEY_LOGIN_MAIL, email)
            prefsEditor.putString(KEY_LOGIN_PASS, password)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun saveSession(context: Context, sessionID: String?): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_SESSION, sessionID)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getSession(context: Context): String? {
        initCache(context)
        mPrefs?.let {
            return it.getString(KEY_SESSION, null)
        }
        return null
    }

    fun saveUser(context: Context, userID: String?): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_USER_ID, userID)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getUserID(context: Context): String? {
        initCache(context)
        mPrefs?.let {
            return it.getString(KEY_USER_ID, null)
        }
        return null
    }


    fun saveAccount(context: Context, accountLogin: String?): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_LOGIN_ACC, accountLogin)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getAccount(context: Context): String? {
        initCache(context)
        mPrefs?.let {
            return it.getString(KEY_LOGIN_ACC, null)
        }
        return null
    }

    fun saveMail(context: Context, accountLogin: String?): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_LOGIN_MAIL, accountLogin)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getMail(context: Context): String? {
        initCache(context)
        mPrefs?.let {
            return it.getString(KEY_LOGIN_MAIL, null)
        }
        return null
    }

    fun savePassword(context: Context, passwordLogin: String?): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putString(KEY_LOGIN_PASS, passwordLogin)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getPassword(context: Context): String? {
        initCache(context)
        mPrefs?.let {
            return it.getString(KEY_LOGIN_PASS, null)
        }
        return null
    }
    /** take a note
                * because columns val as same as grid mode val
                * so we used columns data for cache
                *  */
    fun saveViewLayoutType(context: Context, viewType: Int): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putInt(KEY_VIEW_LAYOUT_TYPE, viewType)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getViewLayoutType(context: Context): Int {
        initCache(context)
        mPrefs?.let {
            return it.getInt(KEY_VIEW_LAYOUT_TYPE, LayoutType.LINEAR)
        }
        return LayoutType.LINEAR
    }

    //////////////////////////////////////////////////////////////////////////////
    /// sync flag
    //////////////////////////////////////////////////////////////////////////////
    fun saveSyncFlag(context: Context, flag: Boolean): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putBoolean(KEY_REQUEST_SYNC, flag)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getSyncFlag(context: Context): Boolean {
        initCache(context)
        mPrefs?.let {
            return it.getBoolean(KEY_REQUEST_SYNC, false)
        }
        return false
    }

    //////////////////////////////////////////////////////////////////////////////
    /// camera setting
    //////////////////////////////////////////////////////////////////////////////

    fun saveCameraSetting(
        context: Context, enableTimeStamp: Boolean,
        cameraFrame: Int,
        pictureQuality: Int,
        cameraTimer: Int,
        cameraFlash: Int
    ): Boolean {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putBoolean(KEY_CAMERA_TIME_STAMP, enableTimeStamp)
            prefsEditor.putInt(KEY_CAMERA_FRAME, cameraFrame)
            prefsEditor.putInt(KEY_CAMERA_QUALITY, pictureQuality)
            prefsEditor.putInt(KEY_CAMERA_TIMER, cameraTimer)
            prefsEditor.putInt(KEY_CAMERA_FLASH, cameraFlash)
            prefsEditor.apply()
            return true
        }
        return false
    }

    fun getTimeStamp(context: Context): Boolean {
        initCache(context)
        return mPrefs?.getBoolean(KEY_CAMERA_TIME_STAMP, false)?:false
    }

    fun getCameraFrame(context: Context): Int {
        initCache(context)
        return mPrefs?.getInt(KEY_CAMERA_FRAME, 0)?:0
    }

    fun getPictureQuality(context: Context): Int {
        initCache(context)
        return mPrefs?.getInt(KEY_CAMERA_QUALITY, 0)?:0
    }

    fun getCameraTimer(context: Context): Int {
        initCache(context)
        return mPrefs?.getInt(KEY_CAMERA_TIMER, 0)?:0
    }
    fun saveCameraTimer(context: Context, newState: Int) {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putInt(KEY_CAMERA_TIMER, newState)
            prefsEditor.apply()
        }
    }

    fun getCameraFlash(context: Context): Int {
        initCache(context)
        return mPrefs?.getInt(KEY_CAMERA_FLASH, 0)?:0
    }
    fun saveCameraFlash(context: Context, newState: Int) {
        initCache(context)
        mPrefs?.let {
            val prefsEditor = it.edit()
            prefsEditor.putInt(KEY_CAMERA_FLASH, newState)
            prefsEditor.apply()
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    ///
    //////////////////////////////////////////////////////////////////////////////

}