package com.yupengw.lua.instruction

import com.yupengw.lua.api.LuaVM

fun move(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    vm.copy(b + 1, a + 1)
}

fun jmp(i: Int, vm: LuaVM) {
    val (a, sBx) = AsBx(i)
    vm.addPC(sBx)
    if (a != 0) throw Exception("TODO!")
}