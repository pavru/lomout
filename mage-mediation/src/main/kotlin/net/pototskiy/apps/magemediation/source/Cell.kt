package net.pototskiy.apps.magemediation.source

interface Cell {
    val address: CellAddress
    val cellType: CellType
    val booleanValue: Boolean
    val intValue: Long
    val doubleValue: Double
    val stringValue: String
    val value: Any
    get() {
        return when(cellType){
            CellType.INT -> intValue
            CellType.DOUBLE -> doubleValue
            CellType.BOOL -> booleanValue
            CellType.STRING -> stringValue
        }
    }
    val row: Row

    fun asString(): String
}