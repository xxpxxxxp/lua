package com.yupengw.lua

import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.api.LuaState
import com.yupengw.lua.vm.LuaStateImpl

fun printStack(ls: LuaState) {
    val top = ls.getTop()
    for (i in 1..top) {
        val t = ls.type(i)
        print("[${when (t) {
            LuaDataType.LUA_TBOOLEAN -> ls.toBoolean(i)
            LuaDataType.LUA_TNUMBER -> ls.toNumber(i)
            LuaDataType.LUA_TSTRING -> ls.toString(i)
            else -> ls.typeName(t)
        }}]")
    }
    println()
}

fun main(args: Array<String>) {
    val ls: LuaState = LuaStateImpl()
    ls.pushBoolean(true); printStack(ls)
    ls.pushInteger(10); printStack(ls)
    ls.pushNil(); printStack(ls)
    ls.pushString("hello"); printStack(ls)
    ls.pushValue(-4); printStack(ls)
    ls.replace(3); printStack(ls)
    ls.setTop(6); printStack(ls)
    ls.remove(-3); printStack(ls)
    ls.setTop(-5); printStack(ls)
}