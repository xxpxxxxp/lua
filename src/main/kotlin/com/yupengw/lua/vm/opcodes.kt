package com.yupengw.lua.vm

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
    val name: String
)

val opcodes = arrayOf(
    /* T    A   B   C   mode    name    */
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "MOVE"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgN, OpCodeType.IABx, "LOADK"),
    OpCode(0, 1, OpArgType.OpArgN, OpArgType.OpArgN, OpCodeType.IABx, "LOADKX"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "LOADBOOL"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "LOADNIL"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "GETUPVAL"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgK, OpCodeType.IABC, "GETTABUP"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgK, OpCodeType.IABC, "GETTABLE"),
    OpCode(0, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SETTABUP"),

    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "SETUPVAL"),
    OpCode(0, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SETTABLE"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "NEWTABLE"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgK, OpCodeType.IABC, "SELF"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "ADD"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SUB"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "MUL"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "MOD"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "POW"),

    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "DIV"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "IDIV"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "BAND"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "BOR"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "BXOR"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SHL"),
    OpCode(0, 1, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SHR"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "UNM"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "BNOT"),

    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "NOT"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IABC, "LEN"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgR, OpCodeType.IABC, "CONCAT"),
    OpCode(0, 0, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "JMP"),
    OpCode(1, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "EQ"),
    OpCode(1, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "LT"),
    OpCode(1, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "LE"),
    OpCode(1, 0, OpArgType.OpArgN, OpArgType.OpArgU, OpCodeType.IABC, "TEST"),
    OpCode(1, 1, OpArgType.OpArgR, OpArgType.OpArgU, OpCodeType.IABC, "TESTSET"),

    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "CALL"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "TAILCALL"),
    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "RETURN"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "FORLOOP"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "FORPREP"),
    OpCode(0, 0, OpArgType.OpArgN, OpArgType.OpArgU, OpCodeType.IABC, "TFORCALL"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "TFORLOOP"),
    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "SETLIST"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABx, "CLOSURE"),

    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "VARARG"),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IAx, "EXTRAARG")
)