package net.pototskiy.apps.magemediation.mediator

object MediatorFactory {
    fun create(type: MediatorType):AbstractMediator{
        return when(type){
            MediatorType.PRODUCT -> TODO()
            MediatorType.CATEGORY -> CategoryMediator()
            MediatorType.PRICE -> TODO()
            MediatorType.INVENTORY -> TODO()
        }
    }
}