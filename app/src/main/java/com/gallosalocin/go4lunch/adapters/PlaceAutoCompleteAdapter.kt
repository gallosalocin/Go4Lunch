package com.gallosalocin.go4lunch.adapters

import android.R
import android.content.Context
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import timber.log.Timber

class PlaceAutoCompleteAdapter(
        context: Context,
        private var placesClient: PlacesClient,
        var token: AutocompleteSessionToken
        )
    : ArrayAdapter<AutocompletePrediction>(context, R.layout.simple_expandable_list_item_1, R.id.text1), Filterable {

    private var mResultList: List<AutocompletePrediction>? = null
    private var tempResult: List<AutocompletePrediction>? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = super.getView(position, convertView, parent)
        val item = getItem(position)
        val textView1 = row.findViewById<TextView>(R.id.text1)
        textView1.text = item.getPrimaryText(STYLE_BOLD)
        return row
    }

    override fun getCount(): Int {
        return mResultList!!.size
    }

    override fun getItem(position: Int): AutocompletePrediction {
        return mResultList!![position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                // Query the autocomplete API for the (constraint) search string.
                mResultList = getAutoComplete(constraint)
                if (mResultList != null) {
                    // The API successfully returned results.
                    results.values = mResultList
                    results.count = mResultList!!.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any): CharSequence {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                return if (resultValue is AutocompletePrediction) {
                    resultValue.getFullText(null)
                } else {
                    super.convertResultToString(resultValue)
                }
            }
        }
    }

    private fun getAutoComplete(constraint: CharSequence): List<AutocompletePrediction>? {
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()
        // Create a RectangularBounds object.

        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build()
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
            for (prediction in response.autocompletePredictions) {
                Timber.i(prediction.getPrimaryText(null).toString())
            }
            tempResult = response.autocompletePredictions
        }.addOnFailureListener { exception: Exception? ->
            if (exception is ApiException) {
                Timber.e("Place not found: %s", exception.statusCode)
            }
        }
        return tempResult
    }

    companion object {
        private val STYLE_BOLD: CharacterStyle = StyleSpan(Typeface.BOLD)
    }
}