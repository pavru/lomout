package net.pototskiy.apps.magemediation.loader

class ExitException(status: Int) : SecurityException("System.exit($status)")
