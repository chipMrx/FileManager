package com.apcc.api

import androidx.lifecycle.LiveData
import com.apcc.utils.DataHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


/**
 * REST API access points using for user and system action
 */
interface ApiService {
    companion object{
        /////////
        const val REGISTER_SESSION = "registerSession"
        const val CHECK_SESSION = "checkSession"
        const val LOG_OUT = "logOut"
        const val GET_CODE = "getCode"
        const val RESET_PASSWORD = "resetPassword"
        const val CHANGE_ACCOUNT = "changeAccount"
        const val CHANGE_MAIL = "changeMail"
        const val CHANGE_PASSWORD = "changePassword"
        const val UPDATE_PROFILE = "updateProfile"

        const val NOTE_SAVE = "noteSave"
        const val NOTE_DELETE = "noteDelete"
        const val NOTE_DELETE_FILE = "noteDeleteFile"
        const val NOTE_GET_DETAIL = "noteGetDetail"
        const val NOTE_GET_ALL = "noteGetAll"
        const val NOTE_GET_LIST = "noteGetList"

        const val FILE_SAVE = "fileSave"
        const val FILE_GET_ALL = "fileGetAll"
        const val FILE_REMOVE ="fileDelete"
        const val FILE_GET_LIST ="fileGetList"

        const val BOOK_SAVE = "bookSave"
        const val BOOK_DELETE = "bookDelete"
        const val BOOK_GET_DETAIL = "bookGetDetail"
        const val BOOK_GET_ALL = "bookGetAll"

        const val LESSON_SAVE = "lessonSave"
        const val LESSON_DELETE = "lessonDelete"
        const val LESSON_GET_DETAIL = "lessonGetDetail"
        const val LESSON_GET_ALL = "lessonGetAll"
        const val LESSON_GET_VIA_BOOK = "lessonGetViaBook"

        const val EXAM_SAVE = "examSave"
        const val EXAM_DELETE = "examDelete"
        const val EXAM_GET_DETAIL = "examGetDetail"
        const val EXAM_GET_ALL = "examGetAll"
        const val EXAM_GET_VIA_BOOK = "examGetViaBook"
        const val EXAM_GET_VIA_LESSON = "examGetViaLesson"

        const val QUESTION_SAVE = "questionSave"
        const val QUESTION_DELETE = "questionDelete"
        const val QUESTION_GET_DETAIL = "questionGetDetail"
        const val QUESTION_GET_ALL = "questionGetAll"
        const val QUESTION_GET_VIA_BOOK = "questionGetViaBook"
        const val QUESTION_GET_VIA_LESSON = "questionGetViaLesson"
        const val QUESTION_GET_VIA_EXAM = "questionGetViaExam"

        const val BLOCK_SAVE = "blockSave"
        const val BLOCK_DELETE = "blockDelete"
        const val BLOCK_GET_DETAIL = "blockGetDetail"
        const val BLOCK_GET_ALL = "blockGetViaAll"
        const val BLOCK_GET_VIA_BOOK = "blockGetViaBook"
        const val BLOCK_GET_VIA_LESSON = "blockGetViaLesson"
        const val BLOCK_GET_VIA_EXAM = "blockGetViaExam"
        const val BLOCK_GET_VIA_QUESTION = "blockGetViaQuestion"


        const val GET_SERVER_CONFIG = "getServerConfig"
        const val SEND_ERRORS = "sendErrors"
        const val LANGUAGE_SAVE = "languageSave"
        const val LANGUAGE_GET_ALL = "languageGetAll"
        const val OPTION_SAVE = "optionSave"
        const val OPTION_DELETE = "optionDelete"
        const val OPTION_GET_ALL = "optionGetAll"

        const val PAYMENT_SERVICE_GET ="paymentServiceGet"
        const val PAYMENT_SERVICE_SAVE ="paymentServiceSave"
        const val PAYMENT_SERVICE_REMOVE ="paymentServiceDelete"

        const val BILL_SAVE = "billSave"
        const val BILL_DELETE = "billDelete"
        const val BILL_DELETE_ITEM = "billDeleteItem"
        const val BILL_DELETE_FILE = "billDeleteFile"
        const val BILL_GET_DETAIL = "billGetDetail"
        const val BILL_GET_ALL = "billGetAll"
        const val BILL_GET_LIST = "billGetList" // list no detail

        const val SUPPLIER_SAVE = "supplierSave"
        const val SUPPLIER_DELETE = "supplierDelete"
        const val SUPPLIER_DELETE_CONTACT = "supplierDeleteContact"
        const val SUPPLIER_DELETE_FILE = "supplierDeleteFile"
        const val SUPPLIER_GET_DETAIL = "supplierGetDetail"
        const val SUPPLIER_GET_ALL = "supplierGetAll"
        const val SUPPLIER_GET_LIST = "supplierGetList"


        const val PRODUCT_SAVE = "productSave"
        const val PRODUCT_DELETE = "productDelete"
        const val PRODUCT_DELETE_FILE = "productDeleteFile"
        const val PRODUCT_DELETE_PRICE = "productDeletePrice"
        const val PRODUCT_GET_DETAIL = "productGetDetail"
        const val PRODUCT_GET_ALL = "productGetAll"
        const val PRODUCT_GET_LIST = "productGetList"

        fun getUrl(pathRequest:String):String{
            return DataHelper.mergeUrl(DataHelper.getSubHost(), pathRequest)
        }
    }


    @Headers("Content-Type:application/json")
    @POST()
    fun postRequest(@Body request: ApiTransfer,
                    @Url url:String): LiveData<ApiResponse<ApiTransfer>>

    ///////////////////////////////////////////////////////
    //// File ///////////////////////////////////////////
    ///////////////////////////////////////////////////////

    /*,
        @Part("SessionID") session: RequestBody,
        @Part("UserID") user: RequestBody,
        @Part("FileApp") fileApp: RequestBody*/
    @Multipart
    @POST()
    fun saveFile(
        @Part part: MultipartBody.Part?,
        @Part("BodyData") time: RequestBody,
        @Url url:String = getUrl(FILE_SAVE)): LiveData<ApiResponse<ApiTransfer>>

}
