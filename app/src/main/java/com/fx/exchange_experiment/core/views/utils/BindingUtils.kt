package com.fx.exchange_experiment.core.views.utils

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import com.fx.exchange_experiment.core.ShadowOutline
import com.fx.exchange_experiment.core.utils.CurrencyUtil
import com.fx.exchange_experiment.rate.model.data.ExchangeRateListItem

object BindingUtils {

    @JvmStatic
    @BindingAdapter("boxShadow")
    fun setBoxShadow(cardView: CardView, @ColorRes color: Int?) {
        if(color == null || color == 0) return
        cardView.outlineProvider = ShadowOutline(cardView, color)
    }

    @JvmStatic
    @BindingAdapter("exchangeItemAmount")
    fun setExchangeAmount(textView: TextView, item: ExchangeRateListItem) {
        val rate = item.exchange.rate
        val amount = item.amount
        val conversion = CurrencyUtil.FORMATTER.format(rate * amount)
        textView.text = conversion
    }

}