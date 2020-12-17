package com.yupengw.lua.vm

import com.yupengw.lua.api.ArithOp
import com.yupengw.lua.api.CompareOp
import com.yupengw.lua.api.LuaDataType
import com.yupengw.lua.api.LuaVM
import com.yupengw.lua.binchunk.unDump
import com.yupengw.lua.instruction.Execute
import com.yupengw.lua.instruction.OpCodeOperator
import com.yupengw.lua.instruction.Opcode
import com.yupengw.lua.state.*
import java.io.InputStream

class LuaStateImpl(stackSize: Int): LuaVM {
    private var stack = LuaStack(stackSize)

    override fun PC(): Int = stack.pc

    override fun addPC(n: Int) { stack.pc += n }

    override fun fetch(): Int = stack.closure!!.proto.code[stack.pc++]

    override fun getConst(idx: Int)  = stack.push(stack.closure!!.proto.constants[idx])

    override fun getRK(rk: Int) =
        if (rk > 0xff) getConst(rk and 0xff)    // constant!
        else pushValue(rk + 1)

    override fun getTop(): Int = stack.top
    override fun absIndex(idx: Int): Int = stack.absIndex(idx)
    override fun checkStack(n: Int): Boolean {
        stack.check(n)
        return true
    }

    override fun pop(n: Int) = repeat(n) { stack.pop() }

    override fun copy(fromIdx: Int, toIdx: Int) = stack.set(toIdx, stack.get(fromIdx))

    override fun pushValue(idx: Int) = stack.push(stack.get(idx))

    override fun replace(idx: Int) = stack.set(idx, stack.pop())

    override fun insert(idx: Int) = rotate(idx, 1)

    override fun remove(idx: Int) {
        rotate(idx, -1)
        pop(1)
    }

    override fun rotate(idx: Int, n: Int) {
        val t = stack.top - 1
        val p = stack.absIndex(idx) - 1
        val m = if (n >= 0) t - n else p - n - 1
        stack.reverse(p, m)
        stack.reverse(m+1, t)
        stack.reverse(p, t)
    }

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

    override fun isInteger(idx: Int): Boolean = stack.get(idx) is Long

    override fun toBoolean(idx: Int): Boolean = convertToBoolean(stack.get(idx))
    override fun toNumberX(idx: Int): Pair<Double, Boolean> = convertToFloat(stack.get(idx))
    override fun toIntegerX(idx: Int): Pair<Long, Boolean> = convertToInteger(stack.get(idx))
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

    override fun setField(idx: Int, k: String) = setTable(stack.get(idx), k, stack.pop())

    override fun setI(idx: Int, i: Long) = setTable(stack.get(idx), i, stack.pop())

    override fun load(chunk: InputStream, chunkName: String, mode: String): Int {
        stack.push(Closure(unDump(chunk)))
        return 0
    }

    override fun call(nArgs: Int, nResults: Int) {
        val c = stack.get(-nArgs-1)
        if (c !is Closure)
            throw Exception("not function!")
        println("call ${c.proto.source}<${c.proto.lineDefined},${c.proto.lastLineDefined}>")
        callLuaClosure(nArgs, nResults, c)
    }

    private fun callLuaClosure(nArgs: Int, nResults: Int, c: Closure) {
        val nRegs = c.proto.maxStackSize
        val nParams = c.proto.numParams
        val isVarargs = c.proto.isVararg.toInt() == 1

        val funcAndArgs = stack.popN(nArgs + 1)
        val newStack =
            if (nArgs > nParams && isVarargs)
                LuaStack(nRegs + 20, c, funcAndArgs.drop(nParams+1))
            else
                LuaStack(nRegs + 20, c)

        newStack.pushN(funcAndArgs.drop(1), nParams.toInt())
        newStack.top = nRegs.toInt()

        pushLuaStack(newStack)
        runLuaClosure()
        popLuaStack()

        if (nResults != 0) {
            val results = newStack.popN(newStack.top - nRegs)
            stack.check(results.size)
            stack.pushN(results, nResults)
        }
    }

    private fun pushLuaStack(stack: LuaStack) {
        stack.prev = this.stack
        this.stack = stack
    }

    private fun popLuaStack() {
        val stack = this.stack
        this.stack = stack.prev!!
        stack.prev = null
    }

    private fun runLuaClosure() {
        while (true) {
            val inst = fetch()
            Execute(inst, this)
            if (Opcode(inst) == OpCodeOperator.OP_RETURN.value)
                break
        }
    }

    override fun registerCount(): Int = stack.closure!!.proto.maxStackSize.toInt()

    override fun loadVararg(n: Int) {
        val c = if (n < 0) stack.varargs.size else n
        stack.check(c)
        stack.pushN(stack.varargs, c)
    }

    override fun loadProto(idx: Int) {
        stack.push(Closure(stack.closure!!.proto.protos[idx]))
    }
}