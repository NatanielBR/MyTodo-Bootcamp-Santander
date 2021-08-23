package com.natanielbr.mytodo.utils

import android.content.Context
import com.natanielbr.mytodo.R
import kotlin.math.absoluteValue


object TypeUtils {

    /**
     * Irá transformar o tempo (em epoch) em algo mais humanizado.
     * Por exemplo:
     * - Se a diferença desse valor com o tempo for em segundos irá retornar "<numero> segundos".
     * - Se a diferença desse valor com o tempo for em minutos irá retornar "<numero> minutos".
     * - Se a diferença desse valor com o tempo for em horas irá retornar "<numero> horas".
     * - Se a diferença desse valor com o tempo for em dias irá retornar "<numero> dias".
     */
    fun Long.humanizeTime(context: Context): String {
        val diff = System.currentTimeMillis() - this

        var time = diff / 1000

        time = time.absoluteValue

        when {
            time < 60 -> { // se é menor que um minuto
                return context.resources.getQuantityString(
                    R.plurals.second_time,
                    time.toInt(),
                    time
                )
            }
            time < 3600 -> { // se é menor que uma hora
                time /= 60 // converte para minutos
                return context.resources.getQuantityString(
                    R.plurals.minute_time,
                    time.toInt(),
                    time
                )
            }
            time < 86400 -> { // se é menor que 24 horas
                time /= 3600 // converte para horas
                return context.resources.getQuantityString(
                    R.plurals.hour_time,
                    time.toInt(),
                    time
                )
            }
            else -> {
                time /= 86400 // converte para dias
                return context.resources.getQuantityString(
                    R.plurals.day_time,
                    time.toInt(),
                    time
                )
            }
        }
    }

}