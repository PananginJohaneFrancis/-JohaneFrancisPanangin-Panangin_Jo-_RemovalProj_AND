package com.eldroid.listings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eldroid.listings.R
import com.eldroid.listings.databinding.FragmentListviewBinding
import com.eldroid.listings.databinding.ItemListviewBinding
import com.eldroid.listings.model.Listing
import com.eldroid.listings.viewmodel.ListingsViewModel

class ListViewFragment : Fragment() {
    private lateinit var binding: FragmentListviewBinding
    private lateinit var viewModel: ListingsViewModel
    private lateinit var adapter: ListingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ListingsViewModel::class.java]

        // Setup ListView with Custom Adapter
        adapter = ListingsAdapter()
        binding.listingsListView.adapter = adapter

        // Observe Listings
        viewModel.listings.observe(viewLifecycleOwner) { listings ->
            adapter.updateListings(listings)
        }

        // Setup Item Click Listener with Popup Menu
        binding.listingsListView.setOnItemClickListener { _, itemView, position, _ ->
            val listing = viewModel.listings.value?.get(position)
            listing?.let { showItemMenu(itemView, it) }
        }
    }

    // Rest of the code remains the same...

    inner class ListingsAdapter : BaseAdapter() {
        private var listings: List<Listing> = listOf()

        fun updateListings(newListings: List<Listing>) {
            listings = newListings
            notifyDataSetChanged()
        }

        override fun getCount() = listings.size

        override fun getItem(position: Int) = listings[position]

        override fun getItemId(position: Int) = listings[position].id.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val binding: ItemListviewBinding = if (convertView == null) {
                ItemListviewBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
            } else {
                ItemListviewBinding.bind(convertView)
            }

            val listing = listings[position]

            // Set listing name
            binding.listingNameTextView.text = listing.name

            // Setup menu button
            binding.menuButton.setOnClickListener { view ->
                showItemMenu(view, listing)
            }

            return binding.root
        }
    }

    private fun showItemMenu(anchorView: View, listing: Listing) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu_list_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    showEditListingDialog(listing)
                    true
                }
                R.id.action_delete -> {
                    viewModel.deleteListing(listing.id)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    private fun showEditListingDialog(listing: Listing) {
        val dialog = AddEditListingDialogFragment.newInstance(
            dialogTitle = getString(R.string.edit_listing),
            isEditMode = true,
            listingId = listing.id,
            currentName = listing.name
        )
        dialog.show(childFragmentManager, "EditListingDialog")
        dialog.isCancelable = false
    }
}
