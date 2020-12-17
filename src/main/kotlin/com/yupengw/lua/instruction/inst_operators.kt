package com.yupengw.lua.instruction

import com.yupengw.lua.api.ArithOp
import com.yupengw.lua.api.CompareOp
import com.yupengw.lua.api.LuaVM

private fun binaryArith(i: Int, vm: LuaVM, op: ArithOp) {
    val (a, b, c) = ABC(i)
    vm.getRK(b)
    vm.getRK(c)
    vm.arith(op)
    vm.replace(a + 1)
}

private fun unaryArith(i: Int, vm: LuaVM, op: ArithOp) {
    val (a, b, _) = ABC(i)
    vm.pushValue(b + 1)
    vm.arith(op)
    vm.replace(a + 1)
}

fun add(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPADD)
fun sub(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPSUB)
fun mul(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPMUL)
fun mod(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPMOD)
fun pow(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPPOW)
fun div(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPDIV)
fun idiv(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPIDIV)
fun band(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPBAND)
fun bor(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPBOR)
fun bxor(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPBXOR)
fun shl(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPSHL)
fun shr(i: Int, vm: LuaVM) = binaryArith(i, vm, ArithOp.LUA_OPSHR)
fun unm(i: Int, vm: LuaVM) = unaryArith(i, vm, ArithOp.LUA_OPUNM)
fun bnot(i: Int, vm: LuaVM) = unaryArith(i, vm, ArithOp.LUA_OPBNOT)

fun len(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    vm.len(b + 1)
    vm.replace(a + 1)
}

fun concat(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    val n = c - b + 1
    vm.checkStack(n)
    for (j in b+1..c+1)
        vm.pushValue(j)

    vm.concat(n)
    vm.replace(a + 1)
}

private fun compare(i: Int, vm: LuaVM, op: CompareOp) {
    val (a, b, c) = ABC(i)

    vm.getRK(b)
    vm.getRK(c)
    if (vm.compare(-2, -1, op) != (a != 0))
        vm.addPC(1)

    vm.pop(2)
}

fun eq(i: Int, vm: LuaVM) = compare(i, vm, CompareOp.LUA_OPEQ)
fun lt(i: Int, vm: LuaVM) = compare(i, vm, CompareOp.LUA_OPLT)
fun le(i: Int, vm: LuaVM) = compare(i, vm, CompareOp.LUA_OPLE)

fun not(i: Int, vm: LuaVM) {
    val (a, b, _) = ABC(i)
    vm.pushBoolean(!vm.toBoolean(b + 1))
    vm.replace(a + 1)
}

fun testSet(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    if (vm.toBoolean(b + 1) == (c != 0)) vm.copy(b + 1, a + 1)
    else vm.addPC(1)
}

fun test(i: Int, vm: LuaVM) {
    val (a, _, c) = ABC(i)
    if (vm.toBoolean(a + 1) != (c != 0))
        vm.addPC(1)
}

fun forPrep(i: Int, vm: LuaVM) {
    val (a, sBx) = AsBx(i)

    // R(A) -= R(A+2)
    vm.pushValue(a+1)
    vm.pushValue(a+3)
    vm.arith(ArithOp.LUA_OPSUB)
    vm.replace(a+1)
    // pc += sBx
    vm.addPC(sBx)
}

fun forLoop(i: Int, vm: LuaVM) {
    val (a, sBx) = AsBx(i)

    // R(A) += R(A+2)
    vm.pushValue(a+3)
    vm.pushValue(a+1)
    vm.arith(ArithOp.LUA_OPADD)
    vm.replace(a+1)

    // R(A) <?= R(A+1)
    val isPositiveStep = vm.toNumber(a+3) >= 0
    if ((isPositiveStep && vm.compare(a+1, a+2, CompareOp.LUA_OPLE)) ||
        (!isPositiveStep && vm.compare(a+2, a+1, CompareOp.LUA_OPLE))) {
        vm.addPC(sBx)       // pc += sBx
        vm.copy(a+1, a+4)   // R(A+3) = R(A)
    }
}