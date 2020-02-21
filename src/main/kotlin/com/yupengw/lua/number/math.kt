package com.yupengw.lua.number

import kotlin.math.floor

// trunk towards negative infinity
fun iFloorDiv(a: Long, b: Long): Long =
    if (a > 0 && b > 0 || a < 0 && b < 0 || a % b == 0L) a / b
    else a / b - 1

fun fFloorDiv(a: Double, b: Double): Double = floor(a / b)

fun iMod(a: Long, b: Long): Long = a - iFloorDiv(a, b) * b
fun fMod(a: Double, b: Double): Double = a - fFloorDiv(a, b) * b

fun shiftLeft(a: Long, n: Long): Long =
    if (n >= 0L) a shl n.toInt()
    else a ushr -n.toInt()

fun shiftRight(a: Long, n: Long) = shiftLeft(a, -n)

fun floatToInteger(f: Double): Pair<Long, Boolean> {
    val l = f.toLong()
    return l to (l.toDouble() == f)
}

fun parseInteger(s: String): Pair<Long, Boolean> =
    s.toLongOrNull().let { (it ?: 0L) to (it != null) }

fun parseFloat(s: String): Pair<Double, Boolean> =
    s.toDoubleOrNull().let { (it ?: 0.0) to (it != null) }