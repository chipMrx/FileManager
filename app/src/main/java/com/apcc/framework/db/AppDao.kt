package com.apcc.framework.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apcc.data.*
import com.apcc.framework.AppManager
import com.apcc.utils.SyncType


@Dao
interface AppDao {


    /////////////////////////////////
    /// file
    /////////////////////////////////

    @Query("SELECT * FROM FileApp WHERE fileAppID = :fileID")
    fun getFile(fileID: String?): LiveData<FileApp?>

    @Query("SELECT * FROM FileApp WHERE createdBy = :userID ORDER BY modifiedDate DESC")
    fun getFiles(userID: String?): LiveData<List<FileApp>?>

    @Query("SELECT * FROM FileApp WHERE fileAppID in (:fileIDs) ORDER BY modifiedDate DESC")
    fun getFiles(fileIDs: List<String>?): LiveData<List<FileApp>?>

    @Query("SELECT * FROM FileApp WHERE createdBy = :userID AND fileType = :type ORDER BY modifiedDate DESC")
    fun getFilesViaType(userID: String?, type:Int): LiveData<List<FileApp>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(fileApp: FileApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFiles(files:List<FileApp>)

    @Query("delete from FileApp where fileAppID in (:fileIDs)")
    fun deleteFile(fileIDs: List<String>?)

    @Query("delete from FileApp where fileAppID = :fileID")
    fun deleteFile(fileID: String?)


    ///////////////////////////////////////////////////////////////////////////////
    /// Error Tracking
    /////////////////////////////////
    @Query("SELECT * FROM ErrorTracking WHERE createdBy = :userID")
    fun getErrors(userID: String?): LiveData<List<ErrorTracking>?>

    @Query("SELECT * FROM ErrorTracking WHERE errorID = :errorID")
    fun getError(errorID: String?): LiveData<ErrorTracking?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertError(errorTracking: ErrorTracking)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertErrors(errors:List<ErrorTracking>)

    @Query("delete from ErrorTracking where errorID in (:errorIDs)")
    fun deleteError(errorIDs: List<String>?)

    @Query("delete from ErrorTracking where errorID = :errorID")
    fun deleteError(errorID: String?)

    ///////////////////////////////////////////////////////////////////////////////
    /// Options
    /////////////////////////////////
    @Query("SELECT * FROM Option WHERE userID = :userID AND optionType = :optionType ORDER BY title")
    fun getOptions(userID: String?, optionType:String): LiveData<List<Option>?>

    @Query("SELECT * FROM Option WHERE optionID = :optionID")
    fun getOption(optionID: String?): LiveData<Option?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOption(option: Option)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOptions(options:List<Option>)

    @Query("delete from Option where optionID in (:optionIDs)")
    fun deleteOption(optionIDs: List<String>?)

    @Query("delete from Option where optionID = :optionID")
    fun deleteOption(optionID: String?)

    ///////////////////////////////////////////////////////////////////////////////
    /// sync
    /////////////////////////////////
    @Query("SELECT * FROM DelaySyncData WHERE userID = :userID ORDER BY createdDate ASC")
    fun getSyncs(userID: String?): LiveData<DelaySyncData?>

    @Query("SELECT * FROM DelaySyncData WHERE userID = :userID ORDER BY createdDate ASC LIMIT 1")
    fun getOldestSync(userID: String?): LiveData<DelaySyncData?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSync(delaySyncData: DelaySyncData)

    fun requestInsertSync(delaySyncData: DelaySyncData){
        when(delaySyncData.requestType){
            SyncType.DELETE->{
                // Highest priority
                deleteSyncViaData(delaySyncData.dataID)
            }
            else ->{
                // remove same request, such as:
                //RequestType.SAVE
                deleteSyncViaData(delaySyncData.dataID, delaySyncData.requestType)
            }
        }
        insertSync(delaySyncData)
        AppManager.updateSync(true, true)
    }

    @Query("delete from DelaySyncData where syncID = :id")
    fun deleteSync(id: String?)

    @Query("delete from DelaySyncData where dataID = :dataID")
    fun deleteSyncViaData(dataID: String?)

    @Query("delete from DelaySyncData where dataID = :dataID AND requestType = :type")
    fun deleteSyncViaData(dataID: String, type:String)


    ///////////////////////////////////////////////////////////////////////////////
    /// functional
    /////////////////////////////////
    fun cleanDB(){

    }
}
