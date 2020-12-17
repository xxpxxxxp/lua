package com.yupengw.lua.api

import java.io.InputStream

interface LuaVM: LuaState {
    // get program counter
    fun PC(): Int

    // jump!
    fun addPC(n: Int)

    // get current instruction and PC++
    fun fetch(): Int

    // push constant to stack
    fun getConst(idx: Int)

    // push constance or stack val to stack
    fun getRK(rk: Int)

    fun load(chunk: InputStream, chunkName: String, mode: String): Int
    fun call(nArgs: Int, nResults: Int)

    fun registerCount(): Int
    fun loadVararg(n: Int)
    fun loadProto(idx: Int)
}