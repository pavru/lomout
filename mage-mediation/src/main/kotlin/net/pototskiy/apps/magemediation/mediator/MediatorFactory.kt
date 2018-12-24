package net.pototskiy.apps.magemediation.mediator

object MediatorFactory {
    fun create(type: MediatorType):AbstractMediator{
        when(type){
            MediatorType.PRODUCT -> TODO()
            MediatorType.CATEGORY -> TODO()
            MediatorType.PRICE -> TODO()
            MediatorType.INVENTORY -> TODO()
        }
    }
}