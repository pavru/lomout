package net.pototskiy.apps.magemediation.api

class ExitException(status: Int): SecurityException("System.exit($status)")
