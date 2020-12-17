package com.yupengw.lua.state

import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.number.floatToInteger
import com.yupengw.lua.number.parseFloat
import com.yupengw.lua.number.parseInteger

fun typeOf(luaValue: Any?): LuaDataType =
    if (luaValue == null) LuaDataType.LUA_TNIL
    else {
        when (luaValue) {
            is Boolean -> LuaDataType.LUA_TBOOLEAN
            is Long, Double -> LuaDataType.LUA_TNUMBER
            is String -> LuaDataType.LUA_TSTRING
            is LuaTable -> LuaDataType.LUA_TTABLE
            is Closure -> LuaDataType.LUA_TFUNCTION
            else -> throw Exception("TODO!")
        }
    }

fun convertToFloat(luaValue: Any?): Pair<Double, Boolean> {
    if (luaValue != null)
        when (luaValue) {
            is Double -> return luaValue to true
            is Long -> return luaValue.toDouble() to true
            is String -> return parseFloat(luaValue)
        }
    return 0.0 to false
}

fun convertToInteger(luaValue: Any?): Pair<Long, Boolean> =
    when (luaValue) {
        is Long -> luaValue to true
        is Double -> floatToInteger(luaValue)
        is String -> stringToInteger(luaValue)
        else -> 0L to false
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