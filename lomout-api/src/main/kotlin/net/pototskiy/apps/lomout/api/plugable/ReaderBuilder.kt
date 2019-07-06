package net.pototskiy.apps.lomout.api.plugable

/**
 * Attribute reader builder interface
 *
 */
interface ReaderBuilder {
    /**
     * Builder reader
     *
     * @return AttributeReader<out Any?>
     */
    fun build(): AttributeReader<out Any?>
}