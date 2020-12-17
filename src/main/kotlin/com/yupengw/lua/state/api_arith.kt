package com.yupengw.lua.state

import com.yupengw.lua.api.ArithOp
import com.yupengw.lua.number.*

class Operator (
    val integerFun: ((Long, Long) -> Long)?,
    val floatFun:  ((Double, Double) -> Double)?
)

val operators = mapOf(
    ArithOp.LUA_OPADD  to Operator({ a: Long, b: Long -> a + b }, { a: Double, b: Double -> a + b }),
    ArithOp.LUA_OPSUB  to Operator({ a: Long, b: Long -> a - b }, { a: Double, b: Double -> a - b }),
    ArithOp.LUA_OPMUL  to Operator({ a: Long, b: Long -> a * b }, { a: Double, b: Double -> a * b }),
    ArithOp.LUA_OPMOD  to Operator(::iMod, ::fMod),
    ArithOp.LUA_OPPOW  to Operator(null, Math::pow),
    ArithOp.LUA_OPDIV  to Operator(null, { a: Double, b: Double -> a / b }),
    ArithOp.LUA_OPIDIV to Operator(::iFloorDiv, ::fFloorDiv),
    ArithOp.LUA_OPBAND to Operator({ a: Long, b: Long -> a and b }, null),
    ArithOp.LUA_OPBOR  to Operator({ a: Long, b: Long -> a or b }, null),
    ArithOp.LUA_OPBXOR to Operator({ a: Long, b: Long -> a xor b }, null),
    ArithOp.LUA_OPSHL  to Operator(::shiftLeft, null),
    ArithOp.LUA_OPSHR  to Operator(::shiftRight, null),
    ArithOp.LUA_OPUNM  to Operator({ a: Long, _: Long -> -a }, { a: Double, _: Double -> -a }),
    ArithOp.LUA_OPBNOT to Operator({ a: Long, _: Long -> a.inv() }, null)
)