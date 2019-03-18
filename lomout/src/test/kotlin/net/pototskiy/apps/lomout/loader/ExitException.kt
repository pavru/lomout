package net.pototskiy.apps.lomout.loader

class ExitException(status: Int) : SecurityException("System.exit($status)")
