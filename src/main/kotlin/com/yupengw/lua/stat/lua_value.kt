package com.yupengw.lua.stat

import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.number.floatToInteger
import com.yupengw.lua.number.parseFloat
import com.yupengw.lua.number.parseInteger
import java.lang.Exception

fun typeOf(luaValue: Any?): LuaDataType =
    if (luaValue == null) LuaDataType.LUA_TNIL
    else {
        when (luaValue::class) {
            Boolean::class -> LuaDataType.LUA_TBOOLEAN
            Long::class, Double::class -> LuaDataType.LUA_TNUMBER
            String::class -> LuaDataType.LUA_TSTRING
            else -> throw Exception("TODO!")
        }
    }

fun convertToFloat(luaValue: Any?): Pair<Double, Boolean> {
    if (luaValue != null)
        when (luaValue::class) {
            Double::class -> return (luaValue as Double) to true
            Long::class -> return (luaValue as Long).toDouble() to true
            String::class -> return parseFloat(luaValue as String)
        }
    return 0.0 to false
}

fun convertToInteger(luaValue: Any?): Pair<Long, Boolean> {
    if (luaValue != null)
        when (luaValue::class) {
            Long::class -> return (luaValue as Long) to true
            Double::class -> return floatToInteger(luaValue as Double)
            String::class -> return stringToInteger(luaValue as String)
        }
    return 0L to false
}

private fun stringToInteger(s: String): Pair<Long, Boolean> {
    parseInteger(s).takeIf { it.second }?.let { return it }
    parseFloat(s).takeIf { it.second }?.let { return floatToInteger(it.first) }
    return 0L to false
}

fun convertToBoolean(luaValue: Any?): Boolean {
    if (luaValue == null) return true
    if (luaValue is Boolean) return luaValue
    return true
}