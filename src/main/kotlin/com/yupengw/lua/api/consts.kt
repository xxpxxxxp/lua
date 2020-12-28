package com.yupengw.lua.api

enum class LuaDataType {
    LUA_TNONE,
    LUA_TNIL,
    LUA_TBOOLEAN,
    LUA_TLIGHTUSERDATA,
    LUA_TNUMBER,
    LUA_TSTRING,
    LUA_TTABLE,
    LUA_TFUNCTION,
    LUA_TUSERDATA,
    LUA_TTHREAD
}

enum class ArithOp {
    LUA_OPADD,      // +
    LUA_OPSUB,      // -
    LUA_OPMUL,      // *
    LUA_OPMOD,      // %
    LUA_OPPOW,      // ^
    LUA_OPDIV,      // /
    LUA_OPIDIV,     // //
    LUA_OPBAND,     // &
    LUA_OPBOR,      // |
    LUA_OPBXOR,     // ~
    LUA_OPSHL,      // <<
    LUA_OPSHR,      // >>
    LUA_OPUNM,      // -
    LUA_OPBNOT      // ~
}

enum class CompareOp {
    LUA_OPEQ,       // ==
    LUA_OPLT,       // <
    LUA_OPLE        // <=
}

const val LUA_MINSTACK = 20
const val LUAI_MAXSTACK = 1000000
const val LUA_REGISTRYINDEX = -LUAI_MAXSTACK - 1000
const val LUA_RIDX_GLOBALS = 2L