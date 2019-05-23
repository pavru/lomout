package net.pototskiy.apps.lomout.api.config.loader

/**
 * Loader collection
 *
 * @property loads Loaders collection
 * @constructor
 */
data class LoadCollection(val loads: List<Load>) : List<Load> by loads
