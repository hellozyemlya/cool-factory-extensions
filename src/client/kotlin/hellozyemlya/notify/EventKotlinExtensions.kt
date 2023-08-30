package hellozyemlya.notify

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory


object EventFactoryKt {
    inline fun <reified T> createArrayBacked(noinline invokerFactory: (Array<T>) -> T): Event<T> {
        return EventFactory.createArrayBacked(T::class.java, invokerFactory)
    }
}