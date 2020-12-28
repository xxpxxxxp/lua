package com.yupengw.lua

import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.api.LuaState
import com.yupengw.lua.api.LuaVM
import com.yupengw.lua.vm.LuaStateImpl
import java.io.FileInputStream

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

fun print(ls: LuaState): Int {
    val nArgs = ls.getTop()
    for (i in 1..nArgs) {
        when {
            ls.isBoolean(i) -> print(ls.toBoolean(i))
            ls.isString(i) -> print(ls.toString(i))
            else -> print(ls.typeName(ls.type(i)))
        }

        if (i < nArgs)
            print("\t")
    }
    println()
    return 0
}

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        FileInputStream(args[0]).use { fis ->
            val ls: LuaVM = LuaStateImpl()
            ls.register("print") { print(it) }
            ls.load(fis, args[0], "b")
            ls.call(0, 0)
        }
    }
}