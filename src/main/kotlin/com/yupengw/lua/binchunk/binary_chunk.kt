package com.yupengw.lua.binchunk

import java.io.InputStream

const val TAG_NIL: Byte = 0x00
const val TAG_BOOLEAN: Byte = 0x01
const val TAG_NUMBER: Byte = 0x03
const val TAG_INTEGER: Byte = 0x13
const val TAG_SHORT_STR: Byte = 0x04
const val TAG_LONG_STR: Byte = 0x14

object Header{
    val LUA_SIGNATURE: ByteArray = byteArrayOf(0x1b, 'L'.toByte(), 'u'.toByte(), 'a'.toByte())
    val LUAC_VERSION: Byte = 0x53
    val LUAC_FORMAT: Byte = 0
    val LUAC_DATA: ByteArray = byteArrayOf(0x19, 0x93.inv().toByte(), '\r'.toByte(), '\n'.toByte(), 0x1a, '\n'.toByte())
    val CINT_SIZE: Byte = 4
    val CSZIET_SIZE: Byte = 8
    val INSTRUCTION_SIZE: Byte = 4
    val LUA_INTEGER_SIZE: Byte = 8
    val LUA_NUMBER_SIZE: Byte = 8
    val LUAC_INT: Long = 0x5678
    val LUAC_NUM: Double = 370.5
}

class Upvalue(
    val inStack: Byte,
    val idx: Byte
)

class LocVar(
    val varName: String,
    val startPC: Int,
    val endPC: Int
)

class Prototype(
    val source: String,
    val lineDefined: Int,
    val lastLineDefined: Int,
    val numParams: Byte,
    val isVararg: Byte,
    val maxStackSize: Byte,
    val code: IntArray,
    val constants: Array<Any?>,
    val upvalues: Array<Upvalue>,
    val protos: Array<Prototype>,
    val lineInfo: IntArray,
    val locVars: Array<LocVar>,
    val upvalueNames: Array<String>
)

class BinaryChunk(val header: Header, val sizeUpValues: Byte, val mainFunc: Prototype)

fun unDump(data: InputStream): Prototype {
    val reader = Reader(data)
    reader.checkHeader()            // verify header
    reader.readByte()               // skip upvalue nums
    return reader.readProto("")     // read Prototype
}

fun printHeader(prototype: Prototype) {
    val funcType = if (prototype.lineDefined > 0) "function"  else "main"
    val varargFlag = if (prototype.isVararg > 0) "+" else ""
    println("$funcType <${prototype.source}:${prototype.lineDefined},${prototype.lastLineDefined}> (${prototype.code.size} instructions)")
    print("${prototype.numParams}$varargFlag params, ${prototype.maxStackSize} slots, ${prototype.upvalues.size} upvalues, ")
    println("${prototype.locVars.size} locals, ${prototype.constants.size} constants, ${prototype.protos.size} functions")
}

fun printCode(prototype: Prototype) {
    for ((i, c) in prototype.code.withIndex()) {
        val line = if (prototype.lineInfo.isEmpty()) "-" else prototype.lineInfo[i].toString()
        println("\t${i+1}\t[$line]\t0x${c.toString(8)}")
    }
}

fun constantToString(k: Any?): String = k?.toString() ?: "nil"

fun printDetail(prototype: Prototype) {
    println("constants (${prototype.constants.size}):")
    for ((i, c) in prototype.constants.withIndex())
        println("\t${i+1}\t${constantToString(c)}")

    println("locals (${prototype.locVars.size})")
    for ((i, v) in prototype.locVars.withIndex())
        println("\t$i\t${v.varName}\t${v.startPC+1}\t${v.endPC+1}")

    println("upvalues (${prototype.upvalues.size})")
    for ((i, u) in prototype.upvalues.withIndex())
        println("\t$i\t${if (prototype.upvalueNames.isNotEmpty()) prototype.upvalueNames[i] else "-"}\t${u.inStack}\t${u.idx}")
}

fun list(prototype: Prototype) {
    printHeader(prototype)
    printCode(prototype)
    printDetail(prototype)
    for (p in prototype.protos) {
        list(p)
    }
}