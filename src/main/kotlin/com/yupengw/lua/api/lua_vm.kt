package com.yupengw.lua.api

interface LuaVM: LuaState {
    fun PC(): Int
    fun addPC(n: Int)           // jump!
    fun fetch(): Int            // get current instruction and PC++
    fun getConst(idx: Int)      // push constant to stack
    fun getRK(rk: Int)          // push constance or stack val to stack
}