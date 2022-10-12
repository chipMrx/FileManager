package com.apcc.repository.resource

import android.text.TextUtils
import android.util.Base64
import com.apcc.framework.AppManager
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

class Encryption {

    fun md5(string: String): String? {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(string.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (item in messageDigest) {
                hexString.append(Integer.toHexString(0xFF and item.toInt()))
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    @Throws(Exception::class)
    fun encrypt3Des(data: String?): String? {
        val key = AppManager.mFlag
        if (TextUtils.isEmpty(key))
            return null
        val keyByte = key.toByteArray()
        return encrypt3Des(keyByte, data)
    }

    @Throws(Exception::class)
    private fun encrypt3Des(keyData: ByteArray, data: String?): String? {
        if (data == null || data.trim { it <= ' ' } == "") return ""
        val key: SecretKey = SecretKeySpec(keyData, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(keyData)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        val stringBytes = data.toByteArray()
        val cipherText = cipher.doFinal(stringBytes)
        var base64 = Base64.encodeToString(ivSpec.iv + cipherText, Base64.DEFAULT)
        base64 = bytesToHex(base64.toByteArray())
        return base64
    }

    fun decrypt3Des(data: String?): String? {
        return try {
            val key = AppManager.mFlag
            if (TextUtils.isEmpty(key))
                return null
            val keyByte = key.toByteArray()
            decrypt3Des(keyByte, data)
        } catch (ex: Exception) {
            ""
        }
    }

    @Throws(Exception::class)
    fun decrypt3Des(keyData: ByteArray, data: String?): String? {
        if (data == null || data.trim { it <= ' ' } == "") return ""
        val key: SecretKey = SecretKeySpec(keyData, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val _data = hexToBytes(data)
        val raw: ByteArray = Base64.decode(_data, Base64.DEFAULT)

        val subIv = raw.copyOfRange (0, keyData.size)
        val rawData = raw.copyOfRange (keyData.size, raw.size)

        val ivSpec = IvParameterSpec(subIv)
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val stringBytes = cipher.doFinal(rawData)
        return String(stringBytes, Charset.forName("UTF8"))
    }

    private fun bytesToHex(data: ByteArray?): String? {
        if (data == null) {
            return null
        }
        val len = data.size
        var str = ""
        for (i in 0 until len) {
            str =
                if ((data[i] and 0xFF.toByte()) < 16) str + "0" + Integer.toHexString((data[i] and 0xFF.toByte()).toInt())
                else str + Integer.toHexString((data[i] and 0xFF.toByte()).toInt())
        }
        return str
    }


    private fun hexToBytes(str: String?): ByteArray? {
        return if (str == null) {
            null
        } else if (str.length < 2) {
            null
        } else {
            val len = str.length / 2
            val buffer = ByteArray(len)
            for (i in 0 until len) {
                buffer[i] = str.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            }
            buffer
        }
    }
}