package com.yupengw.lua

import com.yupengw.lua.api.ArithOp
import com.yupengw.lua.api.CompareOp
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
    ls.pushInteger(1)
    ls.pushString("2.0")
    ls.pushString("3.0")
    ls.pushNumber(4.0)
    printStack(ls)

    ls.arith(ArithOp.LUA_OPADD); printStack(ls)
    ls.arith(ArithOp.LUA_OPBNOT); printStack(ls)
    ls.len(2); printStack(ls)
    ls.concat(3); printStack(ls)
    ls.pushBoolean(ls.compare(1, 2, CompareOp.LUA_OPEQ))
    printStack(ls)
}