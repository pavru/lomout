package net.pototskiy.apps.magemediation.loader

class ExitException(private val status: Int): SecurityException("System.exit($status)")
