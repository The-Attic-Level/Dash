package com.the_attic_level.dash.app

internal typealias Bool = Boolean

internal fun <T:Any> ternary(condition: Bool, a: T, b: T): T {
    return if (condition) a else b
}