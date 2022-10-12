package com.apcc.emma.ui.info

interface InfoActivityCallback {

    fun showTerm(isExclusive:Boolean)
    fun showLicense(isExclusive:Boolean)
    fun showInfo(isExclusive:Boolean)


    fun popBack(className: String)
}