import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.Op

class NotRemovedFilter: SqlFilterPlugin() {
    override fun where(alias: Alias<DbEntityTable>): Op<Boolean> {
        return Op.build { alias[DbEntityTable.currentStatus] neq EntityStatus.REMOVED }
    }
}
