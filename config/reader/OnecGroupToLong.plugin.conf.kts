import java.text.ParseException

class OnecGroupToLong : LongAttributeReader() {
    override fun read(attribute: Attribute<out LONG>, input: Cell): LONG? {
        try {
            return LONG(input.asString().drop(1).stringToLong(locale.createLocale(), false))
        } catch (e: ParseException) {
            throw AppDataException(badPlace(attribute) + input, e.message, e)
        }
    }
}
