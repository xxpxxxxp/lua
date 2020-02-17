package com.yupengw.lua

import com.yupengw.lua.binchunk.list
import com.yupengw.lua.binchunk.unDump
import java.io.FileInputStream

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        FileInputStream(args[0]).use { fis ->
            val proto = unDump(fis)
            list(proto)
        }
    }
}