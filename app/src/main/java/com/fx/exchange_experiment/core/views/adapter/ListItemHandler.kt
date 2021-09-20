package com.fx.exchange_experiment.core.views.adapter

import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * @author Paul Okeke
 */

interface ListItemHandler<T> {
    fun onItemClick(view: View, position: Int, @NonNull item: T)
    fun onItemLongClicked(view: View, position: Int, @NonNull item: T): Boolean = false
    fun onItemSelected(view: View, @Nullable container: View, position: Int, @NonNull item: T) {}
}