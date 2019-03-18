package net.pototskiy.apps.lomout.api

class ExitException(status: Int): SecurityException("System.exit($status)")
