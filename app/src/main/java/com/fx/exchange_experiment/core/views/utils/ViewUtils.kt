package com.fx.exchange_experiment.core.views.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.fx.exchange_experiment.R

object ViewUtils {
    private fun pxToDp(context: Context, pixels: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            pixels.toFloat(),
            context.resources.displayMetrics
        )
            .toInt()
    }
    @JvmStatic
    fun Int.toDp(context: Context): Int {
        return pxToDp(context, this)
    }

    fun RecyclerView.withDefaultListDivider() {
        val d = ContextCompat.getDrawable(context, R.drawable.list_divider)!!
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(d)
        addItemDecoration(itemDecoration)
    }

    fun hideKeyboard(context: Context?, view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}