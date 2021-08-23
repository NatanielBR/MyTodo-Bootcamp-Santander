package com.natanielbr.mytodo.utils

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData

/**
 * Isso é um LiveDate para listas, esse codigo foi baseado em um outro
 * achado na internet mas reescrevi corrigindo alguns detalhes que
 * dificultava pra mim (o valor começava como null e tinha que setar
 * isso antes por que irá ser lançada Exception e criei o metodo transaction()).
 */
class MutableListLiveData<Type> : MutableLiveData<MutableList<Type>>() {

    init {
        super.setValue(mutableListOf())
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
    override fun setValue(l: MutableList<Type>){
        super.setValue(l)
    }

    /**
     * Ira criar abrir um bloco onde é possivel fazer qualquer ação na lista.
     * Apos realizar as alterações, o LiveData será notificada.
     */
    @MainThread
    fun transaction(block: MutableList<Type>.() -> Unit) {
        val value = super.getValue()!!

        block.invoke(value)

        postValue(value)
    }
}