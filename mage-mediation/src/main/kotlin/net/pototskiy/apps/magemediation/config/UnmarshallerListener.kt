package net.pototskiy.apps.magemediation.config

import javax.xml.bind.Unmarshaller

class UnmarshallerListener: Unmarshaller.Listener() {
    override fun afterUnmarshal(target: Any?, parent: Any?) {
        super.afterUnmarshal(target, parent)
        if (target is ConfigValidate) {
            target.validate(parent)
        }
    }
}