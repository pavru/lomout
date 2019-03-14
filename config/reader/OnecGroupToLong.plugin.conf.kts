class OnecGroupToLong : LongAttributeReader() {
    override fun read(attribute: Attribute<out LongType>, input: Cell): LongType? {
        return LongType(input.asString().drop(1).stringToLong(locale.createLocale()))
    }
}
