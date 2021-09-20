package com.fx.exchange_experiment.rate.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.fx.exchange_experiment.R
import com.fx.exchange_experiment.core.network.Resource
import com.fx.exchange_experiment.core.utils.CurrencyUtil
import com.fx.exchange_experiment.core.views.utils.ViewUtils
import com.fx.exchange_experiment.core.views.utils.ViewUtils.withDefaultListDivider
import com.fx.exchange_experiment.databinding.FragmentCurrencyExchangeBinding
import com.fx.exchange_experiment.rate.model.data.ExchangeRateListItem
import com.fx.exchange_experiment.rate.viewmodels.ExchangeRateViewModel
import com.fx.exchange_experiment.rate.views.adapters.ExchangeRateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import java.lang.Exception
import java.lang.NumberFormatException
import kotlin.math.max
import kotlin.math.min

/**
 * A simple [Fragment] subclass.
 * Use the [CurrencyExchangeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CurrencyExchangeFragment : Fragment() {

    private val viewModel: ExchangeRateViewModel by activityViewModels()
    private var uiSubscriptionJob: Job? = null

    private lateinit var layoutBinding: FragmentCurrencyExchangeBinding
    private lateinit var exchangeRateAdapter: ExchangeRateAdapter

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        layoutBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_currency_exchange, parent, false
        )
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutBinding.lifecycleOwner = this.viewLifecycleOwner

        setupAmountField()
        setupCurrencySpinner()
        setupExchangeRecyclerView()
    }

    private fun setupAmountField() {
        layoutBinding.amountText.setOnEditorActionListener { _, _, _ ->
            ViewUtils.hideKeyboard(context, view)
            return@setOnEditorActionListener true
        }
        layoutBinding.amountText.apply {
            setText("${viewModel.exchangeAmount}")
            addTextChangedListener(MoneyTextWatcher(this) {
                try {
                    viewModel.exchangeAmount = it.replace(",", "").toDouble()
                    println("Exchange Amount => ${(viewModel.exchangeAmount)}")
                    subscribeUiToExchangeRates()
                } catch (ex: NumberFormatException) {
                    //Handle
                }
            })
        }
    }

    private fun setupCurrencySpinner() {
        val currencies = CurrencyUtil.getCurrencies()
        layoutBinding.currencyDropDown.adapter = ArrayAdapter(
            requireContext(),
            R.layout.currency_list_item,
            R.id.currencyText,
            currencies
        )

        layoutBinding.currencyDropDown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectedCurrency = currencies[position]
                ViewUtils.hideKeyboard(context, view)
                subscribeUiToExchangeRates()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) = Unit
        }
    }

    private fun setupExchangeRecyclerView() {
        layoutBinding.exchangeList.apply {
            exchangeRateAdapter = ExchangeRateAdapter()
            adapter = exchangeRateAdapter
            withDefaultListDivider()
        }
    }

    private fun subscribeUiToExchangeRates() {
        uiSubscriptionJob?.cancel()
        uiSubscriptionJob = lifecycleScope.launchWhenCreated {
            viewModel.getExchangeRates().collectLatest { responseData ->
                when(responseData) {
                    is Resource.Success -> {
                        val exchangeRates = responseData.data
                        val items = exchangeRates.map { ExchangeRateListItem(it, viewModel.exchangeAmount) }
                        exchangeRateAdapter.dispatchUpdates(items)
                    }
                    else -> Unit
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CurrencyExchangeFragment().apply {}
    }

    /**
     *
     */
    class MoneyTextWatcher constructor(
        private val editText: EditText,
        private val afterTextChange: (amount: String) -> Unit = {}
        ) : TextWatcher {
        private var lastAmount = ""
        private var lastCursorPosition = -1

        private fun cleanAmountString(currencyValue: String?): String {
            return currencyValue?.replace("[(a-z)|(A-Z)|(,. )]".toRegex(), "") ?: ""
        }

        private fun transformToCurrency(value: String): String {
            val parsed = value.toDouble()
            return CurrencyUtil.FORMATTER.format(parsed / 100)
        }

        override fun beforeTextChanged(amount: CharSequence?, start: Int, before: Int, count: Int) {
            if(amount.isNullOrEmpty()) return
            try {
                val cleanString = cleanAmountString(amount.toString())
                lastAmount = transformToCurrency(cleanString)
                lastCursorPosition = editText.selectionStart
            }catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        override fun onTextChanged(amount: CharSequence?, start: Int, before: Int, count: Int) {
            if(amount.toString() == lastAmount) return

            //clear the string
            val cleanAmount = cleanAmountString(amount?.toString() ?: "")
            try {
                val formatAmount = transformToCurrency(cleanAmount)
                editText.removeTextChangedListener(this)
                editText.setText(formatAmount)
                editText.setSelection(formatAmount.length)
                editText.addTextChangedListener(this)

                if(lastCursorPosition != -1 && lastCursorPosition != lastAmount.length) {
                    val delta = formatAmount.length - lastAmount.length
                    val newCursorPos = max(0, min(formatAmount.length, lastCursorPosition + delta))
                    editText.setSelection(newCursorPos)
                }
                afterTextChange(formatAmount)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        override fun afterTextChanged(p0: Editable?) = Unit
    }
}