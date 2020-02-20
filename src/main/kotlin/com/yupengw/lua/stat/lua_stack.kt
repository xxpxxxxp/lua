package com.yupengw.lua.stat

class LuaStack(size: Int) {
    var slots = arrayOfNulls<Any?>(size)
    var top = 0

    fun check(n: Int) {
        if (top + n > slots.size) {
            val tmp = arrayOfNulls<Any?>(top + n)
            System.arraycopy(slots, 0, tmp, 0, slots.size)
            slots = tmp
        }
    }

    fun push(luaValue: Any?) {
        if (top == slots.size) throw Exception("stack overflow!")
        slots[top++] = luaValue
    }

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
}