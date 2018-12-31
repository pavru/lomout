package net.pototskiy.apps.magemediation.loader

import net.pototskiy.apps.magemediation.database.mage.MageCustomerGroup

class MageCustomerGroupLoader: AbstractLoader() {
    override val tableSet = MageCustomerGroup.Companion
}
