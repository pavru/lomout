package net.pototskiy.apps.lomout.api.plugable

/**
 * Attribute writer builder interface
 *
 */
interface WriterBuilder {
    /**
     * Build writer
     *
     * @return AttributeWriter<out Any?>
     */
    fun build(): AttributeWriter<out Any?>
}