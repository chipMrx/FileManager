package com.apcc.comparator

import android.util.Size
import java.util.*
import kotlin.math.*

class CompareSizesByArea : Comparator<Size> {

    private var mViewRatio = 0f
    private var mMoreSquarely = false
    private var isDes = false

    constructor()

    constructor(squarely:Boolean = true, isDes:Boolean = false){
        mMoreSquarely = squarely
        this.isDes = isDes
    }

    constructor(viewRatio:Float){
        mMoreSquarely = false
        mViewRatio = viewRatio
    }

    override fun compare(lhs: Size, rhs: Size): Int {
        if (mViewRatio !=  0f){
            return compareFixedSize(lhs, rhs)
        }
        if (mMoreSquarely)
            return compareMoreSquarely(lhs, rhs)
        return compareNormal(lhs, rhs)
    }

    private fun compareNormal(lhs: Size, rhs: Size): Int {
        return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
    }


    /**
     * Rounds the given value x to an integer towards positive infinity.
     * eg: ceil(0.001f) = 1
     */
    private fun compareMoreSquarely(lhs: Size, rhs: Size):Int{
        // check ratio between 2 size
        var compareRatio = ratio(max(lhs.width, lhs.height), min(lhs.width, lhs.height)) - ratio(max(rhs.width, rhs.height), min(rhs.width, rhs.height))
        // more check to get best size
        if (compareRatio == 0f){
            return if (isDes) compareNormalDes(lhs, rhs)
            else compareNormal(lhs, rhs)
        }
        // round result before convert to integer val to ensure the convert val != 0
        if (compareRatio > 0)// round up
            compareRatio = ceil(compareRatio)
        if (compareRatio < 0)// round down
            compareRatio = floor(compareRatio)

        return compareRatio.toInt()
    }

    private fun compareNormalDes(lhs: Size, rhs: Size): Int {
        return java.lang.Long.signum(
            rhs.width.toLong() * rhs.height - lhs.width.toLong() * lhs.height
        )
    }

    private fun compareFixedSize(lhs: Size, rhs: Size): Int {
        val lRatio = ratio(lhs.width , lhs.height)
        val rRatio = ratio(rhs.width , rhs.height)
        val ratio = (abs(lRatio - mViewRatio) - abs(rRatio - mViewRatio)) * 1000
        val retVal = ratio.toInt()
        if (retVal == 0 ){
            return compareNormalDes(lhs, rhs)
        }
        return retVal
    }


    private fun ratio(numA:Int, numB:Int):Float{
        return numA.toFloat() / numB.toFloat()
    }
}