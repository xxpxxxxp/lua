package com.yupengw.lua.vm

import com.yupengw.lua.api.LuaVM
import java.lang.Exception

const val MAXARG_Bx = (1 shl 18) - 1
const val MAXARG_sBx = MAXARG_Bx shr 1

fun Opcode(instruction: Int): Int = instruction and 0x3f

fun ABC(instruction: Int): Triple<Int, Int, Int> = Triple((instruction shr 6) and 0xff, (instruction shr 23) and 0x1ff, (instruction shr 14) and 0x1ff)

fun ABx(instruction: Int): Pair<Int, Int> = Pair((instruction shr 6) and 0xff, instruction ushr 14)

fun AsBx(instruction: Int): Pair<Int, Int> = ABx(instruction).let { Pair(it.first, it.second - MAXARG_sBx) }

fun Ax(instruction: Int): Int = instruction shr 6

fun OpName(instruction: Int): String = opcodes[Opcode(instruction)].name

fun OpMode(instruction: Int): OpCodeType = opcodes[Opcode(instruction)].opMode

fun BMode(instruction: Int): OpArgType = opcodes[Opcode(instruction)].argBMode

fun CMode(instruction: Int): OpArgType = opcodes[Opcode(instruction)].argCMode

fun Execute(i: Int, vm: LuaVM) {
    val action = opcodes[Opcode(i)].action
    if (action != null) action(i, vm)
    else throw Exception(OpName(i))
}