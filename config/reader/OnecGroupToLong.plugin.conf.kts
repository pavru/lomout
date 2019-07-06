import java.text.ParseException

class OnecGroupToLong : LongAttributeReader(), ReaderBuilder {
    override fun read(attribute: Attribute, input: Cell): Long? {
        try {
            return input.asString().drop(1).stringToLong(locale.createLocale(), false)
        } catch (e: ParseException) {
            throw AppDataException(badPlace(attribute) + input, e.message, e)
        }
    }

    override fun build(): AttributeReader<out Any?> = createReader<OnecGroupToLong>()
}
