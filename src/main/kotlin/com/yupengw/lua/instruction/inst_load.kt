package com.yupengw.lua.instruction

import com.yupengw.lua.api.LuaVM

fun loadNil(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    vm.pushNil()
    for (j in a+1..a+b+1)
        vm.copy(-1, j)

    vm.pop(1)
}

fun loadBool(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    vm.pushBoolean(b != 0)
    vm.replace(a + 1)
    if (c != 0)
        vm.addPC(1)
}

fun loadK(i: Int, vm: LuaVM) {
    val (a, bx) = ABx(i)
    vm.getConst(bx)
    vm.replace(a + 1)
}

fun loadKx(i: Int, vm: LuaVM) {
    val (a, _) = ABx(i)
    val ax = Ax(vm.fetch())
    vm.getConst(ax)
    vm.replace(a + 1)
}