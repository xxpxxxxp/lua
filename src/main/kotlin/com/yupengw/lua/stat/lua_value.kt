package com.yupengw.lua.stat

import com.yupengw.lua.api.LuaDataType
import java.lang.Exception

fun typeOf(luaValue: Any?): LuaDataType =
    if (luaValue == null) LuaDataType.LUA_TNIL
    else {
        when (luaValue::class) {
            Boolean::class -> LuaDataType.LUA_TBOOLEAN
            Long::class, Double::class.java -> LuaDataType.LUA_TNUMBER
            String::class -> LuaDataType.LUA_TSTRING
            else -> throw Exception("TODO!")
        }
    }
