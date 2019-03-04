package net.pototskiy.apps.magemediation.api

import java.io.FileDescriptor
import java.security.Permission

internal class NoExitSecurityManager : SecurityManager() {
    override fun checkPermission(perm: Permission) {
        // allow everything
    }

    override fun checkPermission(perm: Permission, context: Any) {
        // allow everything
    }

    override fun checkRead(fd: FileDescriptor?) {
        // allow everything
    }

    override fun checkRead(file: String?) {
        // allow everything
    }

    override fun checkPropertiesAccess() {
        // allow all
    }

    override fun checkExit(status: Int) {
        if (status != 0)
            throw ExitException(status)
        else
            super.checkExit(status)
    }
}
