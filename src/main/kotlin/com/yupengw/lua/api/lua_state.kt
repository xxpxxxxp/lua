package com.yupengw.lua.api

interface LuaState {
    // basic stack manipulation
    fun getTop(): Int
    fun absIndex(idx: Int): Int
    fun checkStack(n: Int): Boolean
    fun pop(n: Int)
    fun copy(fromIdx: Int, toIdx: Int)
    fun pushValue(idx: Int)
    fun replace(idx: Int)
    fun insert(idx: Int)
    fun remove(idx: Int)
    fun rotate(idx: Int, n: Int)
    fun setTop(idx: Int)

    // access functions
    fun typeName(tp: LuaDataType): String
    fun type(idx: Int): LuaDataType
    fun isNone(idx: Int): Boolean
    fun isNil(idx: Int): Boolean
    fun isNoneOrNil(idx: Int): Boolean
    fun isBoolean(idx: Int): Boolean
    fun isInteger(idx: Int): Boolean
    fun isNumber(idx: Int): Boolean
    fun isString(idx: Int): Boolean
    fun toBoolean(idx: Int): Boolean
    fun toInteger(idx: Int): Long
    fun toIntegerX(idx: Int): Pair<Long, Boolean>
    fun toNumber(idx: Int): Double
    fun toNumberX(idx: Int): Pair<Double, Boolean>
    fun toString(idx: Int): String
    fun toStringX(idx: Int): Pair<String, Boolean>

    // push functions
    fun pushNil()
    fun pushBoolean(b: Boolean)
    fun pushInteger(n: Long)
    fun pushNumber(n: Double)
    fun pushString(s: String)

    fun arith(op: ArithOp)
    fun compare(idx1: Int, idx2: Int, op: CompareOp): Boolean
    fun len(idx: Int)
    fun concat(n: Int)
}