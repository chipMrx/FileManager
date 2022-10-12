package com.apcc.data

class AppPoint {
    constructor()

    constructor(x: Int, y: Int) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    var x:Float = 0f
    var y:Float = 0f

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}