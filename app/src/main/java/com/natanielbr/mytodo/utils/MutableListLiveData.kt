package com.natanielbr.mytodo.utils

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData

class MutableListLiveData<Type> : MutableLiveData<MutableList<Type>>() {

    init {
        value = mutableListOf()
    }

    /**
     * Esse metodo irá acessar de forma direta a lista. Qualquer alteração
     * feita atraves desse metodo não irá ser notificado e os observers
     * não irá ser chamado.
     *
     * @see transaction Use esse metodo para fazer as alterações, atraves dele
     * as alterações serão notificadas para os observers.
     */
    @Deprecated(
        "Não use esse metodo, mas use o transaction()",
        ReplaceWith("this.transaction{  }")
    )
    override fun getValue(): MutableList<Type>? {
        return super.getValue()
    }

    /**
     * Ira criar abrir um container onde é possivel fazer qualquer ação na lista.
     * Apos realizar as alterações, o LiveData será notificada.
     */
    @MainThread
    fun transaction(block: MutableList<Type>.() -> Unit) {
        val value = super.getValue()!!

        block.invoke(value)

        postValue(value)
    }
}