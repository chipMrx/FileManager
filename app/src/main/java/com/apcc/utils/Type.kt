package com.apcc.utils

import androidx.annotation.IntDef
import androidx.annotation.StringDef


@StringDef()
@Retention(AnnotationRetention.SOURCE)
annotation class SyncState {
    companion object {
        const val SYNC_NOT_YET = 1
        const val SYNC_REJECT = 2
    }
}


@StringDef()
@Retention(AnnotationRetention.SOURCE)
annotation class SyncType {
    companion object {
        const val SAVE = "save" /*insert or update*/
        const val DELETE = "delete"
    }
}


@IntDef(FieldLength.ID, FieldLength.MEDIUM, FieldLength.LARGE, FieldLength.HUGE)
@Retention(AnnotationRetention.SOURCE)
annotation class FieldLength {
    companion object {
        const val ID = 50
        const val MEDIUM = 100
        const val LARGE = 200
        const val HUGE = 1000
        const val MAX = Int.MAX_VALUE

    }
}


@IntDef(
    ProtectionLevel.FRIEND_SEE, ProtectionLevel.EVERY_ONE_SEE,
    ProtectionLevel.PRIVATE
)
@Retention(AnnotationRetention.SOURCE)
annotation class ProtectionLevel {
    companion object {
        const val PRIVATE = 0 // no one know that
        const val SPECIAL_SEE = 1 // everybody can see that
        const val SPECIAL_EDIT = 2 // everybody can see that
        const val FRIEND_SEE = 3 // everybody can see that
        const val FRIEND_EDIT = 4 // everybody can see that
        const val EVERY_ONE_SEE = 5 // your friend can see that
        const val EVERY_ONE_EDIT = 6 // your friend can see that


    }
}

@IntDef(BillStatus.INIT,
    BillStatus.SALE_CONFIRM_ORDER,
    BillStatus.SHIP_CHECK,
    BillStatus.SHIP_GOING,
    BillStatus.SHIP_DONE,
    BillStatus.PAY_DONE,
    BillStatus.CANCEL)
@Retention(AnnotationRetention.SOURCE)
annotation class BillStatus {
    companion object {
        const val INIT = 0 // khởi tạo đơn hàng
        const val SALE_CONFIRM_ORDER = 1 // người bán xác nhận đơn hàng
        const val SHIP_CHECK = 2 // đơn vị vận chuyển kiểm hàng và nhận hàng
        const val SHIP_GOING = 4 // đơn vị vận chuyển giao hàng
        const val SHIP_DONE = 8 // đơn đã giao thành công

        const val PAY_DONE = 16 // đơn đã hoàn thành
        const val CANCEL = 32 // đơn bị hủy
    }
}

@IntDef(Gender.UNISEX, Gender.MALE, Gender.FEMALE)
@Retention(AnnotationRetention.SOURCE)
annotation class Gender {
    companion object {
        const val UNISEX = 0
        const val MALE = 1
        const val FEMALE = 2
    }
}

@IntDef(
    FileType.UNKNOW, FileType.IMAGE,
    FileType.DOCUMENT, FileType.SOUND,
    FileType.VIDEO, FileType.FOLDER
)
@Retention(AnnotationRetention.SOURCE)
annotation class FileType {
    companion object {
        const val UNKNOW = 0
        const val IMAGE = 1
        const val DOCUMENT = 2
        const val SOUND = 3
        const val VIDEO = 4
        const val FOLDER = 5
    }
}

@IntDef(
    JobState.OPEN, JobState.RE_OPEN,
    JobState.IN_PROGRESS, JobState.FINISH,
    JobState.CLOSE, JobState.PENDING
)
@Retention(AnnotationRetention.SOURCE)
annotation class JobState {
    companion object {
        const val OPEN = 0
        const val RE_OPEN = 1
        const val IN_PROGRESS = 2
        const val FINISH = 3
        const val CLOSE = 4
        const val PENDING = 5
    }
}

@IntDef(
    ProductState.PROTOTYPE, ProductState.VERSION_A,
    ProductState.VERSION_B, ProductState.RELEASE, ProductState.DEPRECATED
)
@Retention(AnnotationRetention.SOURCE)
annotation class ProductState {
    companion object {
        const val PROTOTYPE = 0
        const val VERSION_A = 1
        const val VERSION_B = 2
        const val RELEASE = 3
        const val DEPRECATED = 4
    }
}

//@IntDef(PriceType.SALE, PriceType.BUY)
//@Retention(AnnotationRetention.SOURCE)
//annotation class PriceType {
//    companion object {
//        const val SALE = 0
//        const val BUY = 1
//    }
//}

@StringDef(
    SessionType.UNKNOW, SessionType.WEB,
    SessionType.APP_ANDROID, SessionType.APP_IOS,
    SessionType.WEB_ANDROID, SessionType.WEB_IOS
)
@Retention(AnnotationRetention.SOURCE)
annotation class SessionType {
    companion object {
        const val UNKNOW = "nnknow"
        const val WEB = "web"
        const val APP_ANDROID = "appAndroid"
        const val APP_IOS = "appIos"
        const val WEB_ANDROID = "webAndroid"
        const val WEB_IOS = "webIos"
    }
}

/**
 * Chars, max length = 10
 */
@StringDef(
    RequestExceptionType.REQUEST_RESET_PASSWORD,
    RequestExceptionType.REQUEST_CHANGE_ACCOUNT,
    RequestExceptionType.REQUEST_CHANGE_EMAIL
)
@Retention(AnnotationRetention.SOURCE)
annotation class RequestExceptionType {
    companion object {
        const val REQUEST_RESET_PASSWORD = "rq_rs_pw"
        const val REQUEST_CHANGE_ACCOUNT = "rq_ch_acc"
        const val REQUEST_CHANGE_EMAIL = "rq_ch_mail"
    }
}


////////////////////////////////////////
// using for view
/////////////////////////////////////////
@IntDef(
    TextType.TEXT_HTML,

    TextType.TEXT_NORMAL,
    TextType.TEXT_BIG,
    TextType.TEXT_HUGE,
    TextType.TEXT_SMALL,

    TextType.TEXT_NORMAL_ACCENT,
    TextType.TEXT_BIG_ACCENT,
    TextType.TEXT_HUGE_ACCENT,
    TextType.TEXT_SMALL_ACCENT,

    TextType.TEXT_NORMAL_SUCCESS,
    TextType.TEXT_BIG_SUCCESS,
    TextType.TEXT_HUGE_SUCCESS,
    TextType.TEXT_SMALL_SUCCESS,

    TextType.TEXT_NORMAL_IMPORTANT,
    TextType.TEXT_BIG_IMPORTANT,
    TextType.TEXT_HUGE_IMPORTANT,
    TextType.TEXT_SMALL_IMPORTANT,

    TextType.TEXT_NORMAL_DISABLE,
    TextType.TEXT_BIG_DISABLE,
    TextType.TEXT_HUGE_DISABLE,
    TextType.TEXT_SMALL_DISABLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class TextType {
    companion object {
        const val TEXT_HTML = 0

        /*normal*/
        const val TEXT_NORMAL = 1
        const val TEXT_BIG = 2
        const val TEXT_HUGE = 3
        const val TEXT_SMALL = 4

        /*accent*/
        const val TEXT_NORMAL_ACCENT = 5
        const val TEXT_BIG_ACCENT = 6
        const val TEXT_HUGE_ACCENT = 7
        const val TEXT_SMALL_ACCENT = 8

        /*success*/
        const val TEXT_NORMAL_SUCCESS = 9
        const val TEXT_BIG_SUCCESS = 10
        const val TEXT_HUGE_SUCCESS = 11
        const val TEXT_SMALL_SUCCESS = 12

        /*important*/
        const val TEXT_NORMAL_IMPORTANT = 13
        const val TEXT_BIG_IMPORTANT = 14
        const val TEXT_HUGE_IMPORTANT = 15
        const val TEXT_SMALL_IMPORTANT = 16

        /*disable*/
        const val TEXT_NORMAL_DISABLE = 17
        const val TEXT_BIG_DISABLE = 18
        const val TEXT_HUGE_DISABLE = 19
        const val TEXT_SMALL_DISABLE = 20
    }
}

@IntDef(
    LayoutType.LINEAR,
    LayoutType.GRID_2,
    LayoutType.GRID_3,
    LayoutType.GRID_4,
    LayoutType.GRID_5,
    LayoutType.GRID_6
)
@Retention(AnnotationRetention.SOURCE)
annotation class LayoutType {
    companion object {
        const val LINEAR = 0
        const val GRID_2 = 2
        const val GRID_3 = 3
        const val GRID_4 = 4
        const val GRID_5 = 5
        const val GRID_6 = 6
    }
}


@IntDef(
    RepeatType.NONE,
    RepeatType.DAILY,
    RepeatType.WEEKLY,
    RepeatType.MONTHLY,
    RepeatType.YEARLY
)
@Retention(AnnotationRetention.SOURCE)
annotation class RepeatType {
    companion object {
        const val NONE = 1 // no repeat, do once time
        const val DAILY = 2
        const val WEEKLY = 4
        const val MONTHLY = 8
        const val YEARLY = 16
    }
}

@StringDef(
    CustomOptionType.SIM_CARD_PROVIDER,
    CustomOptionType.SIM_CARD_TYPE,

    CustomOptionType.BILL_TYPE,

    CustomOptionType.OTHER_APP_LIST_TYPE
)
@Retention(AnnotationRetention.SOURCE)
annotation class CustomOptionType {
    companion object {
        const val SIM_CARD_PROVIDER = "simCardProvider"
        const val SIM_CARD_TYPE = "simCardType"

        const val BILL_TYPE = "orderType"

        const val OTHER_APP_LIST_TYPE = "otherAppListType"
    }
}
