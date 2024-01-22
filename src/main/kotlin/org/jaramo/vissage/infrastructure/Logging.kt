package org.jaramo.vissage.infrastructure

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

object Logging {
    fun getLogger(kClass: KClass<*>): Logger = LoggerFactory.getLogger(kClass.java)

//    inline fun <reified T : Any> getLogger(): Logger = getLogger(T::class)

    /**
     * Returns a logger for the class T.
     * If called from a companion object, the parent class is used.
     */
    inline fun <reified T : Any> T.getLoggerForClass(): Logger =
        if (T::class.isCompanion) getLogger(T::class.java.declaringClass::class)
        else getLogger(T::class)

}
