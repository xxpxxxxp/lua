package com.yupengw.lua.vm

import com.yupengw.lua.api.LuaVM

/* converts an integer to a "floating point byte", represented as
 * (eeeeexxx), where the real value is (1xxx) * 2 ^ (eeeee - 1) if eeeee != 0
 * or (xxx) otherwise
 */
private fun int2Fb(x: Int): Int {
    if (x < 8) return x
    var v = x
    var e = 0
    while (v >= (8 shl 4)) {
        v = (v + 0xf) shr 4
        e += 4
    }
    while (v >= (8 shl 1)) {
        v = (v + 1) shr 1
        e++
    }

    return ((e + 1) shl 3) or (v - 8)
}

private fun fb2Int(x: Int): Int =
    if (x < 8) x
    else ((x and 7) + 8) shl ((x shr 3) - 1)

fun newTable(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    vm.createTable(fb2Int(b), fb2Int(c))
    vm.replace(a + 1)
}

fun getTable(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)

    vm.getRK(c)
    vm.getTable(b + 1)
    vm.replace(a + 1)
}

fun setTable(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    vm.getRK(b)
    vm.getRK(c)
    vm.setTable(a + 1)
}

const val LFIELDS_PRE_FLUSH = 50L

fun setList(i: Int, vm: LuaVM) {
    val (a, b, c) = ABC(i)
    val table = a + 1

    val batch = if (c > 0) c - 1 else Ax(vm.fetch())
    var idx = batch * LFIELDS_PRE_FLUSH
    for (j in 1..b) {
        vm.pushValue(table + j)
        vm.setI(table, ++idx)
    }
}