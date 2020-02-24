package com.yupengw.lua.vm

import com.yupengw.lua.api.LuaVM
import java.lang.Exception

fun move(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    vm.copy(b + 1, a + 1)
}

fun jmp(i: Int, vm: LuaVM) {
    val (a, sBx) = AsBx(i)
    vm.addPC(sBx)
    if (a != 0) throw Exception("TODO!")
}