package com.yupengw.lua.instruction

import com.yupengw.lua.api.LuaVM

fun closure(i: Int, vm: LuaVM) {
    val (a, bx) = ABx(i)
    vm.loadProto(bx)
    vm.replace(a+1)
}

fun call(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    val nArgs = pushFuncAndArgs(a+1, b, vm)
    vm.call(nArgs, c-1)
    popResults(a+1, c, vm)
}

private fun pushFuncAndArgs(a: Int, b: Int, vm: LuaVM): Int =
    if (b >= 1) {
        // b-1 args
        vm.checkStack(b)
        for (i in a until a+b)
            vm.pushValue(i)
        b-1
    } else {
        fixStack(a, vm)
        vm.getTop() - vm.registerCount() - 1
    }


private fun popResults(a: Int, c: Int, vm: LuaVM) {
    if (c > 1) {
        // c-1 results
        for (i in a+c-2 downTo a)
            vm.replace(i)
    } else if (c < 1) {
        vm.checkStack(1)
        vm.pushInteger(a.toLong())
    }
}

private fun fixStack(a: Int, vm: LuaVM) {
    val x = vm.toInteger(-1).toInt()
    vm.pop(1)
    vm.checkStack(x - a)
    for (i in a until x)
        vm.pushValue(i)
    vm.rotate(vm.registerCount()+1, x-a)
}

fun _return(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)

    if (b > 1) {
        // b-1 return values
        vm.checkStack(b-1)
        for (j in a+1 until a+b)
            vm.pushValue(j)
    } else if (b < 1) {
        fixStack(a+1, vm)
    }
}

fun vararg(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    if (b != 1) {
        vm.loadVararg(b-1)
        popResults(a+1, b, vm)
    }
}

fun tailCall(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    val nArgs = pushFuncAndArgs(a+1, b, vm)
    vm.call(nArgs, -1)
    popResults(a+1, 0, vm)
}

fun self(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    vm.copy(b+1, a+2)
    vm.getRK(c)
    vm.getTable(b+1)
    vm.replace(a+1)
}