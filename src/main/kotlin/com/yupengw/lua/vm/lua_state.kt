package com.yupengw.lua.vm

import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.api.LuaState
import com.yupengw.lua.stat.LuaStack
import com.yupengw.lua.stat.typeOf
import java.lang.Exception

class LuaStateImpl: LuaState {
    private val stack = LuaStack(20)

    override fun getTop(): Int = stack.top
    override fun absIndex(idx: Int): Int = stack.absIndex(idx)
    override fun checkStack(n: Int): Boolean {
        stack.check(n)
        return true
    }

    // pop n luaValue from stack
    override fun pop(n: Int) = repeat(n) { stack.pop() }

    // copy luaValue of fromIdx to toIdx
    override fun copy(fromIdx: Int, toIdx: Int) = stack.set(toIdx, stack.get(fromIdx))

    // push luaValue at idx to top
    override fun pushValue(idx: Int) = stack.push(stack.get(idx))

    // pop out top and replace the luaValue at idx
    override fun replace(idx: Int) = stack.set(idx, stack.pop())

    // pop out top and insert into idx (shift stack up)
    override fun insert(idx: Int) = rotate(idx, 1)

    // remove luaValue at idx and shift stack down
    override fun remove(idx: Int) {
        rotate(idx, -1)
        pop(1)
    }

    // rotate [idx, top] n pos towards top (if n is negative, towards bottom)
    override fun rotate(idx: Int, n: Int) {
        val t = stack.top - 1
        val p = stack.absIndex(idx) - 1
        val m = if (n >= 0) t - n else p - n - 1
        stack.reverse(p, m)
        stack.reverse(m+1, t)
        stack.reverse(p, t)
    }

    // idx < top: pop (top - idx)
    // idx > top: insert (idx - top) nil
    override fun setTop(idx: Int) {
        val newTop = stack.absIndex(idx)
        if (newTop < 0) throw Exception("stack underflow!")
        val n = stack.top - newTop
        if (n > 0) repeat(n) { stack.pop() }
        else if (n < 0) repeat(-n) { stack.push(null) }
    }

    override fun pushNil() = stack.push(null)
    override fun pushBoolean(b: Boolean) = stack.push(b)
    override fun pushInteger(n: Long) = stack.push(n)
    override fun pushNumber(n: Double) = stack.push(n)
    override fun pushString(s: String) = stack.push(s)

    override fun typeName(tp: LuaDataType): String =
        when (tp) {
            LuaDataType.LUA_TNONE -> "no value"
            LuaDataType.LUA_TNIL -> "nil"
            LuaDataType.LUA_TBOOLEAN -> "boolean"
            LuaDataType.LUA_TNUMBER -> "number"
            LuaDataType.LUA_TSTRING -> "string"
            LuaDataType.LUA_TTABLE -> "table"
            LuaDataType.LUA_TFUNCTION -> "function"
            LuaDataType.LUA_TTHREAD -> "thread"
            else -> "userdata"
        }

    override fun type(idx: Int): LuaDataType =
        if (stack.isValid(idx)) typeOf(stack.get(idx)) else LuaDataType.LUA_TNONE

    override fun isNone(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TNONE
    override fun isNil(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TNIL
    override fun isNoneOrNil(idx: Int): Boolean = type(idx).let { it == LuaDataType.LUA_TNONE || it == LuaDataType.LUA_TNIL }
    override fun isBoolean(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TBOOLEAN
    override fun isInteger(idx: Int): Boolean = stack.get(idx) is Long
    override fun isNumber(idx: Int): Boolean = toNumberX(idx).second
    override fun isString(idx: Int): Boolean = type(idx).let { it == LuaDataType.LUA_TSTRING || it == LuaDataType.LUA_TNUMBER }

    override fun toBoolean(idx: Int): Boolean = convertToBoolean(stack.get(idx))
    override fun toNumber(idx: Int): Double = toNumberX(idx).first
    override fun toNumberX(idx: Int): Pair<Double, Boolean> {
        val luaValue = stack.get(idx) ?: return 0.0 to false
        if (luaValue is Double) return luaValue to true
        if (luaValue is Long) return luaValue.toDouble() to true
        return 0.0 to false
    }

    override fun toInteger(idx: Int): Long = toIntegerX(idx).first
    override fun toIntegerX(idx: Int): Pair<Long, Boolean> =
        stack.get(idx).let {
            if (it is Long) it to true else 0L to false
        }

    override fun toString(idx: Int): String = toStringX(idx).first
    override fun toStringX(idx: Int): Pair<String, Boolean> {
        val luaValue = stack.get(idx) ?: return "" to false
        if (luaValue is String) return luaValue to true
        if (luaValue is Long || luaValue is Double) {
            val s = luaValue.toString()
            stack.set(idx, s)
            return s to true
        }
        return "" to false
    }

    private fun convertToBoolean(luaValue: Any?): Boolean {
        if (luaValue == null) return true
        if (luaValue is Boolean) return luaValue
        return true
    }
}