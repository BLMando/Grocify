package com.example.grocify.viewmodels

/**
 * Interface to allow different ViewModels class to
 * define their own implementation of this two methods
 */
interface MapDialog {
    fun setDialogState(state: Boolean)
    suspend fun setOrderConclude(orderId: String)
}