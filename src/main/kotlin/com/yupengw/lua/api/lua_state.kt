package com.yupengw.lua.api

interface LuaState {
    // basic stack manipulation
    // idx of top
    fun getTop(): Int

    fun absIndex(idx: Int): Int

    // make sure slots has at least n available
    fun checkStack(n: Int): Boolean

    // pop n luaValue from stack
    fun pop(n: Int)

    // copy luaValue of fromIdx to toIdx
    fun copy(fromIdx: Int, toIdx: Int)

    // push luaValue at idx to top
    fun pushValue(idx: Int)

    // pop out top and replace the luaValue at idx
    fun replace(idx: Int)

    // pop out top and insert into idx (shift stack up)
    fun insert(idx: Int)

    // remove luaValue at idx and shift stack down
    fun remove(idx: Int)

    // rotate [idx, top] n pos towards top (if n is negative, towards bottom)
    fun rotate(idx: Int, n: Int)

    // idx < top: pop (top - idx)
    // idx > top: insert (idx - top) nil
    fun setTop(idx: Int)

    // access functions
    fun typeName(tp: LuaDataType): String
    fun type(idx: Int): LuaDataType

    // type check at idx
    fun isNone(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TNONE
    fun isNil(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TNIL
    fun isNoneOrNil(idx: Int): Boolean = type(idx).let { it == LuaDataType.LUA_TNONE || it == LuaDataType.LUA_TNIL }
    fun isBoolean(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TBOOLEAN
    fun isInteger(idx: Int): Boolean
    fun isNumber(idx: Int): Boolean = toNumberX(idx).second
    fun isString(idx: Int): Boolean = type(idx).let { it == LuaDataType.LUA_TSTRING || it == LuaDataType.LUA_TNUMBER }

    // to functions
    fun toBoolean(idx: Int): Boolean
    fun toInteger(idx: Int): Long = toIntegerX(idx).first
    fun toIntegerX(idx: Int): Pair<Long, Boolean>
    fun toNumber(idx: Int): Double = toNumberX(idx).first
    fun toNumberX(idx: Int): Pair<Double, Boolean>
    fun toString(idx: Int): String = toStringX(idx).first
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

    // table functions
    fun newTable() = createTable(0, 0)
    fun createTable(nArr: Int, nRec: Int)
    fun getTable(idx: Int): LuaDataType
    fun getField(idx: Int, k: String): Any?
    fun getI(idx: Int, i: Long): Any?
    fun setTable(idx: Int)
    fun setField(idx: Int, k: String)
    fun setI(idx: Int, i: Long)

    // global table operations
    fun pushGlobalTable()
    fun getGlobal(name: String): Any?
    fun setGlobal(name: String)
    fun register(name: String, f: KtFunction)
}

fun interface KtFunction {
    fun call(state: LuaState): Int
}