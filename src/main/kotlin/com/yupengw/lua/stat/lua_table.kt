package com.yupengw.lua.stat

import com.yupengw.lua.number.floatToInteger
import java.lang.Exception

class LuaTable(nArr: Int, nRec: Int) {
    private val arr: MutableList<Any?> = MutableList(nArr){ null }
    private val map: MutableMap<Any, Any?> = LinkedHashMap(nRec)

    private fun _floatToInteger(key: Any?): Any? {
        if (key != null && key is Double) {
            val (i, ok) = floatToInteger(key)
            if (ok) return i
        }

        return key
    }

    fun get(key: Any?): Any? {
        val k = _floatToInteger(key)
        if (k is Long && k in 1..arr.size) return arr[(k-1).toInt()]
        return map[k]
    }

    fun put(key: Any?, value: Any?) {
        if (key == null) throw Exception("table index is null!")
        if (key is Double && key.isNaN()) throw Exception("table index is NaN!")
        val k = _floatToInteger(key)

        if (k is Long && k >= 1) {
            val arrLen = arr.size
            if (k <= arrLen) {
                arr[(k-1).toInt()] = value
                if (k == arrLen && value == null) {
                    // remove tailing hole
                    while (arr.isNotEmpty() && arr.last() == null) {
                        arr.removeAt(arr.lastIndex)
                    }
                }
                return
            }

            if (k == arrLen + 1) {
                map.remove(key)

                if (value != null) {
                    arr.add(value)
                    var idx = arr.size + 1
                    while (idx in map) {
                        arr.add(map[idx]!!)
                        map.remove(idx)
                        idx++
                    }
                }

                return
            }
        }

        if (value != null) map[key] = value
        else map.remove(key)
    }

    fun len(): Long = arr.size.toLong()
}