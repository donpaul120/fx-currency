package com.fx.exchange_experiment.rate.views.adapters

import com.fx.exchange_experiment.core.views.adapter.EXDataBindingAdapter
import com.fx.exchange_experiment.R
import com.fx.exchange_experiment.rate.model.data.ExchangeRateListItem

class ExchangeRateAdapter : EXDataBindingAdapter<ExchangeRateListItem>(arrayListOf()) {
    override fun getItemViewType(position: Int): Int {
        return R.layout.exchange_list_item
    }
}