package net.pototskiy.apps.magemediation.loader

class LoaderFactory {
    companion object {
        fun create(type: LoadDestination): LoaderInterface {
            return when (type) {
                LoadDestination.MAGE_PRODUCT -> MagentoProductLoader()
                LoadDestination.ONEC_PRODUCT -> OnecProductLoader()
                LoadDestination.ONEC_CATEGORY -> OnecGroupLoader()
                LoadDestination.MAGE_CATEGORY -> MagentoCategoryLoader()
                LoadDestination.MAGE_USER_GROUP -> MageCustomerGroupLoader()
                LoadDestination.MAGE_PRICING -> MagentoAdvPriceLoader()
                LoadDestination.MAGE_INVENTORY -> MagentoStockSourceLoader()
                LoadDestination.ONEC_CATEGORY_RELATION -> OnecGroupRelationLoader()
            }
        }
    }
}