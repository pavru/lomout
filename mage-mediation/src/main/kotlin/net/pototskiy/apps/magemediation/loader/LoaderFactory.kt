package net.pototskiy.apps.magemediation.loader

class LoaderFactory {
    companion object {
        fun create(type: LoadDestination): LoaderInterface {
            return when (type) {
                LoadDestination.MAGE_PRODUCT -> MagentoProductLoader()
                LoadDestination.ONEC_PRODUCT -> OnecProductLoader()
                LoadDestination.ONEC_CATEGORY -> OnecGroupLoader()
                else -> throw LoaderException("Can not create data loader")
            }
        }
    }
}