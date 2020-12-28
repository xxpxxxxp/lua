package com.yupengw.lua.instruction

import com.yupengw.lua.api.LuaVM

enum class OpCodeType {
    IABC, IABx, IAsBx, IAx
}

enum class OpCodeOperator(val value: Int) {
    OP_MOVE(0),
    OP_LOADK(1),
    OP_LOADKX(2),
    OP_LOADBOOL(3),
    OP_LOADNIL(4),
    OP_GETUPVAL(5),
    OP_GETTABUP(6),
    OP_GETTABLE(7),
    OP_SETTABUP(8),
    OP_SETUPVAL(9),
    OP_SETTABLE(10),
    OP_NEWTABLE(11),
    OP_SELF(12),
    OP_ADD(13),
    OP_SUB(14),
    OP_MUL(15),
    OP_MOD(16),
    OP_POW(17),
    OP_DIV(18),
    OP_IDIV(19),
    OP_BAND(20),
    OP_BOR(21),
    OP_BXOR(22),
    OP_SHL(23),
    OP_SHR(24),
    OP_UNM(25),
    OP_BNOT(26),
    OP_NOT(27),
    OP_LEN(28),
    OP_CONCAT(29),
    OP_JMP(30),
    OP_EQ(31),
    OP_LT(32),
    OP_LE(33),
    OP_TEST(34),
    OP_TESTSET(35),
    OP_CALL(36),
    OP_TAILCALL(37),
    OP_RETURN(38),
    OP_FORLOOP(39),
    OP_FORPREP(40),
    OP_TFORCALL(41),
    OP_TFORLOOP(42),
    OP_SETLIST(43),
    OP_CLOSURE(44),
    OP_VARARG(45),
    OP_EXTRAARG(46)
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
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgK, OpCodeType.IABC, "GETTABUP", ::getTabUp),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgK, OpCodeType.IABC, "GETTABLE", ::getTable),
    OpCode(0, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SETTABUP"),

    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "SETUPVAL"),
    OpCode(0, 0, OpArgType.OpArgK, OpArgType.OpArgK, OpCodeType.IABC, "SETTABLE", ::setTable),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "NEWTABLE", ::newTable),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgK, OpCodeType.IABC, "SELF", ::self),
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

    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "CALL", ::call),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "TAILCALL", ::tailCall),
    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "RETURN", ::_return),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "FORLOOP", ::forLoop),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "FORPREP", ::forPrep),
    OpCode(0, 0, OpArgType.OpArgN, OpArgType.OpArgU, OpCodeType.IABC, "TFORCALL"),
    OpCode(0, 1, OpArgType.OpArgR, OpArgType.OpArgN, OpCodeType.IAsBx, "TFORLOOP"),
    OpCode(0, 0, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IABC, "SETLIST", ::setList),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABx, "CLOSURE", ::closure),

    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgN, OpCodeType.IABC, "VARARG", ::vararg),
    OpCode(0, 1, OpArgType.OpArgU, OpArgType.OpArgU, OpCodeType.IAx, "EXTRAARG")
)