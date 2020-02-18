package com.yupengw.lua.binchunk

import java.io.InputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Reader(private val data: InputStream){
    fun readByte(): Byte = data.read().toByte()

    fun readInt(): Int = ByteBuffer.wrap(data.readNBytes(4)).order(ByteOrder.LITTLE_ENDIAN).int

    fun readLong(): Long = ByteBuffer.wrap(data.readNBytes(8)).order(ByteOrder.LITTLE_ENDIAN).long

    fun readLuaInteger(): Long = readLong()

    fun readLuaNumber(): Double = ByteBuffer.wrap(data.readNBytes(8)).order(ByteOrder.LITTLE_ENDIAN).double

    fun readString(): String {
        var size = data.read()
        when (size) {
            0 -> return ""
            0xff -> size = readLong().toInt()
        }

        return String(data.readNBytes(size - 1))
    }

    fun readBytes(n: Int): ByteArray = data.readNBytes(n)

    fun checkHeader() {
        if (!readBytes(4).contentEquals(Header.LUA_SIGNATURE))
            throw Exception("not a precompiled chunk!")

        if (readByte() != Header.LUAC_VERSION)
            throw Exception("version mismatch!")

        if (readByte() != Header.LUAC_FORMAT)
            throw Exception("format mismatch!")

        if (readBytes(6).contentEquals(Header.LUAC_DATA))
            throw Exception("corrupted!")

        if (readByte() != Header.CINT_SIZE)
            throw Exception("int size mismatch!")

        if (readByte() != Header.CSZIET_SIZE)
            throw Exception("size_t size mismatch!")

        if (readByte() != Header.INSTRUCTION_SIZE)
            throw Exception("instruction size mismatch!")

        if (readByte() != Header.LUA_INTEGER_SIZE)
            throw Exception("lua_Integer size mismatch!")

        if (readByte() != Header.LUA_NUMBER_SIZE)
            throw Exception("lua_Number size mismatch!")

        if (readLuaInteger() != Header.LUAC_INT)
            throw Exception("endianness mismatch!")

        if (readLuaNumber() != Header.LUAC_NUM)
            throw Exception("float format mismatch!")
    }

    fun readCode(): IntArray = IntArray(readInt()) { readInt() }

    fun readConstant(): Any? =
        when (readByte()) {
            TAG_NIL -> null
            TAG_BOOLEAN -> readByte() != 0.toByte()
            TAG_INTEGER -> readLuaInteger()
            TAG_NUMBER -> readLuaNumber()
            TAG_SHORT_STR, TAG_LONG_STR -> readString()
            else -> throw Exception("corrupted!")
        }

    fun readConstants(): Array<Any?> = Array(readInt()) { readConstant() }

    fun readUpvalues(): Array<Upvalue> = Array(readInt()) { Upvalue(readByte(), readByte()) }

    fun readProtos(parentSource: String): Array<Prototype> = Array(readInt()) { readProto(parentSource) }

    fun readLineInfo(): IntArray = IntArray(readInt()) { readInt() }

    fun readLocVars(): Array<LocVar> = Array(readInt()) { LocVar(readString(), readInt(), readInt()) }

    fun readUpValueNames(): Array<String> = Array(readInt()) { readString() }

    fun readProto(parentSource: String): Prototype {
        var source = readString()
        if (source.isEmpty())
            source = parentSource

        return Prototype(
            source,
            readInt(),
            readInt(),
            readByte(),
            readByte(),
            readByte(),
            readCode(),
            readConstants(),
            readUpvalues(),
            readProtos(source),
            readLineInfo(),
            readLocVars(),
            readUpValueNames()
        )
    }
}