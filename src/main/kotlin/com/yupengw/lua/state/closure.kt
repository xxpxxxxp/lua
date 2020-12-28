package com.yupengw.lua.state

import com.yupengw.lua.api.KtFunction
import com.yupengw.lua.binchunk.Prototype

data class Closure(val proto: Prototype? = null, val ktFunc: KtFunction? = null)