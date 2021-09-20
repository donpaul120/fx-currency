package com.fx.exchange_experiment.rate.model.data

data class Currency(
    val name: String,
    val code: String
) {

    companion object {
         val USD = Currency("US Dollars", "USD")
         val NGN = Currency("Nigerian Naira", "NGN")
         val JPY = Currency("Japanese Yen", "JPY")
    }

    override fun toString(): String {
        return this.code
    }
}