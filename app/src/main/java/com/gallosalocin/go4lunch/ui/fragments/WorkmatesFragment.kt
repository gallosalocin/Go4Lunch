package com.gallosalocin.go4lunch.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.adapters.WorkmateAdapter
import com.gallosalocin.go4lunch.databinding.FragmentWorkmatesBinding
import com.gallosalocin.go4lunch.models.Workmate
import com.gallosalocin.go4lunch.util.Constants.CHOSEN_RESTAURANT_ID_FIELD
import com.gallosalocin.go4lunch.util.Constants.WORKMATES_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class WorkmatesFragment : Fragment(R.layout.fragment_workmates) {

    private var _binding: FragmentWorkmatesBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val workmateCollectionRef = db.collection(WORKMATES_COLLECTION)
    private var navController: NavController? = null
    private var mAdapter: WorkmateAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkmatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        setupRecyclerView()
    }

    // Setup RecyclerView
    private fun setupRecyclerView() {
        val query: Query = workmateCollectionRef.orderBy(CHOSEN_RESTAURANT_ID_FIELD, Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Workmate>().setQuery(query, Workmate::class.java).build()

        mAdapter = WorkmateAdapter(
                options,
                onItemClickListener = {
                    if (it.chosenRestaurantId == "") {
                        Toast.makeText(requireContext(), getString(R.string.toast_workmate_not_decided, it.name), Toast.LENGTH_SHORT).show()
                    } else {
                        val action = WorkmatesFragmentDirections.actionWorkmatesFragmentToDetailsFragment(null, it.chosenRestaurantId)
                        navController!!.navigate(action)
                    }
                })

        binding.rvWorkmates.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    // Setup Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        menu.findItem(R.id.tb_menu_search_btn).isVisible = false
        menu.findItem(R.id.tb_menu_sort_btn).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.tb_menu_search_btn) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        mAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter!!.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}