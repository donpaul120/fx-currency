package com.fx.exchange_experiment.core

import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.fx.exchange_experiment.core.views.utils.ViewUtils.toDp

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ShadowOutline(private val cardView: CardView,  private val color: Int) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline?) {
        outline?.setRoundRect((-2).toDp(view.context), (-8).toDp(view.context), cardView.width, cardView.height, cardView.radius)
        //let's equally change the shadow color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cardView.outlineAmbientShadowColor = color//cardView.context.getColor(color)
            cardView.outlineSpotShadowColor = color//cardView.context.getColor(color)
        }
    }
}