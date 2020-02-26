package com.yupengw.lua.vm

import com.yupengw.lua.api.LuaVM

enum class OpCodeType {
    IABC, IABx, IAsBx, IAx
}

enum class OpCodeOperator {
    OP_MOVE,
    OP_LOADK,
    OP_LOADKX,
    OP_LOADBOOL,
    OP_LOADNIL,
    OP_GETUPVAL,
    OP_GETTABUP,
    OP_GETTABLE,
    OP_SETTABUP,
    OP_SETUPVAL,
    OP_SETTABLE,
    OP_NEWTABLE,
    OP_SELF,
    OP_ADD,
    OP_SUB,
    OP_MUL,
    OP_MOD,
    OP_POW,
    OP_DIV,
    OP_IDIV,
    OP_BAND,
    OP_BOR,
    OP_BXOR,
    OP_SHL,
    OP_SHR,
    OP_UNM,
    OP_BNOT,
    OP_NOT,
    OP_LEN,
    OP_CONCAT,
    OP_JMP,
    OP_EQ,
    OP_LT,
    OP_LE,
    OP_TEST,
    OP_TESTSET,
    OP_CALL,
    OP_TAILCALL,
    OP_RETURN,
    OP_FORLOOP,
    OP_FORPREP,
    OP_TFORCALL,
    OP_TFORLOOP,
    OP_SETLIST,
    OP_CLOSURE,
    OP_VARARG,
    OP_EXTRAARG
}

enum class OpArgType {
    OpArgN,     // arg not used
    OpArgU,     // arg is used
    OpArgR,     // arg is a register or a jump offset
    OpArgK      // arg is a constant or register/constant
}

class OpCode (
    val testFlag: Byte,         // operator is a test (next instruction must be a jump)
    val setAFlag: Byte,         // instruction set register A
    val argBMode: OpArgType,    // B arg mode
    val argCMode: OpArgType,    // C arg mode
    val opMode: OpCodeType,     // op mode
    val name: String,
    val action: ((i: Int, vm: LuaVM) -> Unit)? = null
)

val opcodes = arrayOf(
    /* T    A   B   C   mode    name    */
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "MOVE", ::move),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgN, OpCodeType.IABx, "LOADK", ::loadK),
    OpCode(0, 1, OpArgType.OpArgN, OpArgType.OpArgN, OpCodeType.IABx, "LOADKX", ::loadKx),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "LOADBOOL", ::loadBool),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "LOADNIL", ::loadNil),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "GETUPVAL"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgK, OpCodeType.IABC, "GETTABUP"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgK, OpCodeType.IABC, "GETTABLE", ::getTable),
    OpCode(0, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SETTABUP"),

    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "SETUPVAL"),
    OpCode(0, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SETTABLE", ::setTable),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "NEWTABLE", ::newTable),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgK, OpCodeType.IABC, "SELF"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "ADD", ::add),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SUB", ::sub),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "MUL", ::mul),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "MOD", ::mod),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "POW", ::pow),

    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "DIV", ::div),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "IDIV", ::idiv),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "BAND", ::band),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "BOR", ::bor),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "BXOR", ::bxor),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SHL", ::shl),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SHR", ::shr),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "UNM", ::unm),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "BNOT", ::bnot),

    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "NOT", ::not),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "LEN", ::len),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgR, OpCodeType.IABC, "CONCAT", ::concat),
    OpCode(0, 0, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "JMP", ::jmp),
    OpCode(1, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "EQ", ::eq),
    OpCode(1, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "LT", ::lt),
    OpCode(1, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "LE", ::le),
    OpCode(1, 0, OpArgType.OpArgN, OpArgType.OpArgU, OpCodeType.IABC, "TEST", ::test),
    OpCode(1, 1, OpArgType.OpArgR, OpArgType.OpArgU, OpCodeType.IABC, "TESTSET", ::testSet),

    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "CALL"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "TAILCALL"),
    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "RETURN"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "FORLOOP", ::forLoop),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "FORPREP", ::forPrep),
    OpCode(0, 0, OpArgType.OpArgN, OpArgType.OpArgU, OpCodeType.IABC, "TFORCALL"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "TFORLOOP"),
    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "SETLIST", ::setList),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABx, "CLOSURE"),

    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "VARARG"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IAx, "EXTRAARG")
)