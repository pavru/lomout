package net.pototskiy.apps.magemediation

@Suppress("unused")
fun cctu(str: String): String {
    val builder = StringBuilder()
    for (c in str) {
        if (c.isUpperCase()) {
            builder.append('_')
            builder.append(c.toLowerCase())
        } else {
            builder.append(c)
        }
    }
    return builder.toString()
}

@Suppress("unused")
fun utcc(str: String): String {
    val builder = StringBuilder()
    var upperNext = false
    for (c in str) {
        if (c == '_') {
            upperNext = true
        } else {
            if (upperNext) {
                builder.append(c.toUpperCase())
                upperNext = false
            } else {
                builder.append(c)
            }
        }
    }
    return builder.toString()
}