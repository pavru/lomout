package net.pototskiy.apps.magemediation.api.plugable

data class Parameter<T : Any>(val name: String, val default: T? = null) {
    override fun equals(other: Any?): Boolean = if (other is Parameter<*>) {
        name == other.name
    } else {
        false
    }

    override fun hashCode(): Int = name.hashCode()
    override fun toString(): String = "Parameter($name)"
}
