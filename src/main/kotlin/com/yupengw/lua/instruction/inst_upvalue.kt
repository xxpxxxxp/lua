package com.yupengw.lua.instruction

import com.yupengw.lua.api.LuaVM

fun getTabUp(i: Int, vm: LuaVM) {
    val (a, _, c) = ABC(i)

    vm.pushGlobalTable()
    vm.getRK(c)
    vm.getTable(-2)
    vm.replace(a+1)
    vm.pop(1)
}