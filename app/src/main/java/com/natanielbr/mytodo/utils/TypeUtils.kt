package com.natanielbr.mytodo.utils

import android.content.Context
import android.text.format.DateFormat.getDateFormat
import com.natanielbr.mytodo.R
import java.text.DateFormat
import java.util.*


object TypeUtils {

    /**
     * Irá transformar o tempo (em epoch) em algo mais humanizado.
     * Por exemplo:
     * - Se a diferença desse valor com o tempo for em segundos irá retornar "<numero> segundos".
     * - Se a diferença desse valor com o tempo for em minutos irá retornar "<numero> minutos".
     * - Se a diferença desse valor com o tempo for em horas irá retornar "<numero> horas".
     * - Se a diferença desse valor com o tempo for em dias irá retornar "<numero> dias".
     * - Se os dias for maior que 3, ele irá exibir a data no formato dd/mm/yyyy
     */
    fun Long.humanizeTime(context: Context): String {
        val diff = System.currentTimeMillis() - this

        var time = diff / 1000

        when {
            time < 60 -> { // se é menor que um minuto
                return context.getString(R.string.second_time, time)
            }
            time < 3600 -> { // se é menor que uma hora
                time /= 60 // converte para minutos
                return context.getString(R.string.minute_time, time)
            }
            time < 86400 -> { // se é menor que 24 horas
                time /= 3600 // converte para horas
                return context.getString(R.string.hour_time, time)
            }
            time < 259200 -> { // se é menor que 3 dias
                time /= 86400 // converte para dias
                return context.getString(R.string.day_time, time)
            }
            else -> {
                return formatDate(context)
            }
        }
    }

    fun Long.formatDate(context: Context): String {
        val date = Date(this)
        val dateFormat: DateFormat = getDateFormat(context)!!
        return dateFormat.format(date)
    }
}