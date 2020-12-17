package com.yupengw.lua.state

class LuaStack(
    size: Int,
    val closure: Closure? = null,
    val varargs: List<Any?> = listOf(),
) {
    var slots = arrayOfNulls<Any?>(size)
    var top = 0

    var prev: LuaStack? = null
    var pc: Int = 0

    // make sure slots has at least n available
    fun check(n: Int) {
        if (top + n > slots.size) {
            val tmp = arrayOfNulls<Any?>(top + n)
            System.arraycopy(slots, 0, tmp, 0, slots.size)
            slots = tmp
        }
    }

    // push value to top
    fun push(luaValue: Any?) {
        if (top == slots.size) throw Exception("stack overflow!")
        slots[top++] = luaValue
    }

    // pop top value
    fun pop(): Any? {
        if (top < 1) throw Exception("stack underflow!")
        top--
        val luaValue = slots[top]
        slots[top] = null
        return luaValue
    }

    fun absIndex(idx: Int): Int =
        if (idx >= 0) idx else idx + top + 1

    fun isValid(idx: Int): Boolean =
        absIndex(idx) in 1..top

    fun get(idx: Int): Any? {
        val absIdx = absIndex(idx)
        return if (absIdx in 1..top) slots[absIdx-1] else null
    }

    fun set(idx: Int, luaValue: Any?) {
        val absIdx = absIndex(idx)
        if (absIdx !in 1..top)
            throw Exception("invalid index!")

        slots[absIdx-1] = luaValue
    }

    fun reverse(from: Int, to: Int) {
        var f = from
        var t = to
        while (f < t) {
            val tmp = slots[f]
            slots[f] = slots[t]
            slots[t] = tmp
            f++
            t--
        }
    }

    fun popN(n: Int): List<Any?> = List(n) { pop() }.reversed()

    fun pushN(vals: List<Any?>, n: Int) {
        val len = vals.size
        val size = if (n < 0) len else n
        for (i in 0 until size) {
            push(if (i < len) vals[i] else null)
        }
    }
}