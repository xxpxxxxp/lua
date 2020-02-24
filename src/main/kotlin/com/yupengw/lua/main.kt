package com.yupengw.lua

import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.api.LuaState
import com.yupengw.lua.api.LuaVM
import com.yupengw.lua.binchunk.Prototype
import com.yupengw.lua.binchunk.unDump
import com.yupengw.lua.vm.Execute
import com.yupengw.lua.vm.LuaStateImpl
import com.yupengw.lua.vm.OpName
import com.yupengw.lua.vm.Opcode
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

fun luaMain(proto: Prototype) {
    val nRegs = proto.maxStackSize.toInt()
    val ls: LuaVM = LuaStateImpl(nRegs + 8, proto)
    ls.setTop(nRegs)

    while (true) {
        val pc = ls.PC()
        val inst = ls.fetch()

        if (Opcode(inst) != 38) {
            Execute(inst, ls)
            print("%02d ${OpName(inst)} ".format(pc+1))
            printStack(ls)
        } else break
    }
}

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val proto = FileInputStream(args[0]).use { fis ->
            unDump(fis)
        }
        luaMain(proto)
    }
}