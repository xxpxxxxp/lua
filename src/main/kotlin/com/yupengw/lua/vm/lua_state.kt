package com.yupengw.lua.vm

import com.yupengw.lua.api.*
import com.yupengw.lua.binchunk.Prototype
import com.yupengw.lua.stat.*
import java.lang.Exception

class LuaStateImpl(stackSize: Int, private val proto: Prototype): LuaVM {
    private val stack = LuaStack(stackSize)
    private var pc: Int = 0

    override fun getTop(): Int = stack.top
    override fun absIndex(idx: Int): Int = stack.absIndex(idx)
    override fun checkStack(n: Int): Boolean {
        stack.check(n)
        return true
    }

    // pop n luaValue from stack
    override fun pop(n: Int) = repeat(n) { stack.pop() }

    // copy luaValue of fromIdx to toIdx
    override fun copy(fromIdx: Int, toIdx: Int) = stack.set(toIdx, stack.get(fromIdx))

    // push luaValue at idx to top
    override fun pushValue(idx: Int) = stack.push(stack.get(idx))

    // pop out top and replace the luaValue at idx
    override fun replace(idx: Int) = stack.set(idx, stack.pop())

    // pop out top and insert into idx (shift stack up)
    override fun insert(idx: Int) = rotate(idx, 1)

    // remove luaValue at idx and shift stack down
    override fun remove(idx: Int) {
        rotate(idx, -1)
        pop(1)
    }

    // rotate [idx, top] n pos towards top (if n is negative, towards bottom)
    override fun rotate(idx: Int, n: Int) {
        val t = stack.top - 1
        val p = stack.absIndex(idx) - 1
        val m = if (n >= 0) t - n else p - n - 1
        stack.reverse(p, m)
        stack.reverse(m+1, t)
        stack.reverse(p, t)
    }

    // idx < top: pop (top - idx)
    // idx > top: insert (idx - top) nil
    override fun setTop(idx: Int) {
        val newTop = stack.absIndex(idx)
        if (newTop < 0) throw Exception("stack underflow!")
        val n = stack.top - newTop
        if (n > 0) repeat(n) { stack.pop() }
        else if (n < 0) repeat(-n) { stack.push(null) }
    }

    override fun pushNil() = stack.push(null)
    override fun pushBoolean(b: Boolean) = stack.push(b)
    override fun pushInteger(n: Long) = stack.push(n)
    override fun pushNumber(n: Double) = stack.push(n)
    override fun pushString(s: String) = stack.push(s)

    override fun typeName(tp: LuaDataType): String =
        when (tp) {
            LuaDataType.LUA_TNONE -> "no value"
            LuaDataType.LUA_TNIL -> "nil"
            LuaDataType.LUA_TBOOLEAN -> "boolean"
            LuaDataType.LUA_TNUMBER -> "number"
            LuaDataType.LUA_TSTRING -> "string"
            LuaDataType.LUA_TTABLE -> "table"
            LuaDataType.LUA_TFUNCTION -> "function"
            LuaDataType.LUA_TTHREAD -> "thread"
            else -> "userdata"
        }

    override fun type(idx: Int): LuaDataType =
        if (stack.isValid(idx)) typeOf(stack.get(idx)) else LuaDataType.LUA_TNONE

    override fun isNone(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TNONE
    override fun isNil(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TNIL
    override fun isNoneOrNil(idx: Int): Boolean = type(idx).let { it == LuaDataType.LUA_TNONE || it == LuaDataType.LUA_TNIL }
    override fun isBoolean(idx: Int): Boolean = type(idx) == LuaDataType.LUA_TBOOLEAN
    override fun isInteger(idx: Int): Boolean = stack.get(idx) is Long
    override fun isNumber(idx: Int): Boolean = toNumberX(idx).second
    override fun isString(idx: Int): Boolean = type(idx).let { it == LuaDataType.LUA_TSTRING || it == LuaDataType.LUA_TNUMBER }

    override fun toBoolean(idx: Int): Boolean = convertToBoolean(stack.get(idx))
    override fun toNumber(idx: Int): Double = toNumberX(idx).first
    override fun toNumberX(idx: Int): Pair<Double, Boolean> = convertToFloat(stack.get(idx))

    override fun toInteger(idx: Int): Long = toIntegerX(idx).first
    override fun toIntegerX(idx: Int): Pair<Long, Boolean> = convertToInteger(stack.get(idx))

    override fun toString(idx: Int): String = toStringX(idx).first
    override fun toStringX(idx: Int): Pair<String, Boolean> {
        val luaValue = stack.get(idx) ?: return "" to false
        if (luaValue is String) return luaValue to true
        if (luaValue is Long || luaValue is Double) {
            val s = luaValue.toString()
            stack.set(idx, s)
            return s to true
        }
        return "" to false
    }

    override fun arith(op: ArithOp) {
        val b = stack.pop()
        val a = if (op != ArithOp.LUA_OPUNM && op != ArithOp.LUA_OPBNOT) stack.pop() else b

        val operator = operators.getValue(op)
        stack.push(arith(a, b, operator) ?: throw Exception("arithmetic error!"))
    }

    private fun arith(a: Any?, b: Any?, op: Operator): Any? {
        if (op.floatFun == null) {  // bitwise
            convertToInteger(a).let { (i, ok1) ->
                convertToInteger(b).let { (j, ok2)  ->
                    if (ok1 && ok2)
                        return op.integerFun!!.invoke(i, j)
                }
            }
        } else {
            if (op.integerFun != null && a is Long && b is Long) {    // add, sub, mul, mod, idiv, unm
                return op.integerFun.invoke(a, b)
            }
            convertToFloat(a).let { (i, ok1) ->
                convertToFloat(b).let { (j, ok2)  ->
                    if (ok1 && ok2)
                        return op.floatFun.invoke(i, j)
                }
            }
        }

        return null
    }

    override fun compare(idx1: Int, idx2: Int, op: CompareOp): Boolean {
        val a = stack.get(idx1)
        val b = stack.get(idx2)

        when (op) {
            CompareOp.LUA_OPEQ -> {
                return if (a == null || b == null)  a == null && b == null
                else when (a) {
                    is Boolean -> b is Boolean && b == a
                    is String -> b is String && b == a
                    is Long ->
                        when (b) {
                            is Long -> a == b
                            is Double -> a.toDouble() == b
                            else -> false
                        }
                    is Double ->
                        when (b) {
                            is Double -> a == b
                            is Long -> a == b.toDouble()
                            else -> false
                        }
                    else -> a == b
                }
            }
            CompareOp.LUA_OPLT -> {
                if (a != null && b != null) {
                    when (a) {
                        is String -> if (b is String) return a < b
                        is Long ->
                            when (b) {
                                is Long -> return a < b
                                is Double -> return a.toDouble() < b
                            }
                        is Double ->
                            when (b) {
                                is Double -> return a < b
                                is Long -> return a < b.toDouble()
                            }
                    }
                }
                throw Exception("comparison error!")
            }
            CompareOp.LUA_OPLE -> {
                if (a != null && b != null) {
                    when (a) {
                        is String -> if (b is String) return a <= b
                        is Long ->
                            when (b) {
                                is Long -> return a <= b
                                is Double -> return a.toDouble() <= b
                            }
                        is Double ->
                            when (b) {
                                is Double -> return a <= b
                                is Long -> return a <= b.toDouble()
                            }
                    }
                }
                throw Exception("comparison error!")
            }
        }
    }

    override fun len(idx: Int) {
        val luaValue = stack.get(idx)
        stack.push(
            when (luaValue) {
                is String -> luaValue.length.toLong()
                is LuaTable -> luaValue.len()
                else -> throw Exception("length error!")
            }
        )
    }

    override fun concat(n: Int) {
        when (n) {
            0 -> stack.push("")
            1 -> { /* ignore */ }
            else -> {
                repeat(n-1) {
                    if (!isString(-1) || !isString(-2)) throw Exception("concatenation error!")
                    val s2 = toString(-1)
                    val s1 = toString(-2)
                    stack.pop()
                    stack.pop()
                    stack.push(s1 + s2)
                }
            }
        }
    }

    override fun PC(): Int = pc

    override fun addPC(n: Int) {
        pc += n
    }

    override fun fetch(): Int = proto.code[pc++]

    override fun getConst(idx: Int)  = stack.push(proto.constants[idx])

    override fun getRK(rk: Int) =
        if (rk > 0xff) getConst(rk and 0xff)    // constant!
        else pushValue(rk + 1)

    override fun createTable(nArr: Int, nRec: Int) {
        stack.push(LuaTable(nArr, nRec))
    }

    private fun getTable(table: Any?, key: Any?): Any? {
        if (table is LuaTable) {
            val value = table.get(key)
            stack.push(value)
            return typeOf(value)
        }
        throw Exception("not a table!")
    }

    override fun getTable(idx: Int): Any? {
        val table = stack.get(idx)
        val key = stack.pop()
        return getTable(table, key)
    }

    override fun getField(idx: Int, k: String): Any? = getTable(stack.get(idx), k)

    override fun getI(idx: Int, i: Long): Any? = getTable(stack.get(idx), i)

    private fun setTable(table: Any?, key: Any?, value: Any?) {
        if (table is LuaTable) {
            table.put(key, value)
            return
        }
        throw Exception("not a table!")
    }

    override fun setTable(idx: Int) {
        val table = stack.get(idx)
        val value = stack.pop()
        val key = stack.pop()
        setTable(table, key, value)
    }

    override fun setFeild(idx: Int, k: String) = setTable(stack.get(idx), k, stack.pop())

    override fun setI(idx: Int, i: Long) = setTable(stack.get(idx), i, stack.pop())
}