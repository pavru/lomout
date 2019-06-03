import java.text.ParseException

class OnecGroupToLong : LongAttributeReader() {
    override fun read(attribute: Attribute<out LongType>, input: Cell): LongType? {
        try {
            return LongType(input.asString().drop(1).stringToLong(locale.createLocale(), false))
        } catch (e: ParseException) {
            throw AppDataException(badPlace(attribute) + input, e.message, e)
        }
    }
}
