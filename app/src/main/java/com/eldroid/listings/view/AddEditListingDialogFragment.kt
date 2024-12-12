package com.eldroid.listings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.eldroid.listings.R
import com.eldroid.listings.databinding.DialogAddEditItemBinding
import com.eldroid.listings.viewmodel.ListingsViewModel

class AddEditListingDialogFragment : DialogFragment() {
    private lateinit var binding: DialogAddEditItemBinding
    private lateinit var viewModel: ListingsViewModel

    companion object {
        private const val ARG_DIALOG_TITLE = "dialog_title"
        private const val ARG_IS_EDIT_MODE = "is_edit_mode"
        private const val ARG_LISTING_ID = "listing_id"
        private const val ARG_CURRENT_NAME = "current_name"

        fun newInstance(
            dialogTitle: String,
            isEditMode: Boolean,
            listingId: Int? = null,
            currentName: String? = null
        ): AddEditListingDialogFragment {
            val fragment = AddEditListingDialogFragment()
            val args = Bundle().apply {
                putString(ARG_DIALOG_TITLE, dialogTitle)
                putBoolean(ARG_IS_EDIT_MODE, isEditMode)
                listingId?.let { putInt(ARG_LISTING_ID, it) }
                currentName?.let { putString(ARG_CURRENT_NAME, it) }
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window
        val params = window?.attributes

        // Get screen width and set dialog width to 80% of the screen width
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        params?.width = width

        window?.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ListingsViewModel::class.java]

        // Set Dialog Title
        val dialogTitle = arguments?.getString(ARG_DIALOG_TITLE)
        binding.dialogFragmentLabel.text = dialogTitle

        // Populate current name for edit mode
        val isEditMode = arguments?.getBoolean(ARG_IS_EDIT_MODE) ?: false
        if (isEditMode) {
            val currentName = arguments?.getString(ARG_CURRENT_NAME)
            binding.editListingNameEditText.setText(currentName)
        }

        // Cancel Button
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        // Confirm Button
        binding.confirmButton.setOnClickListener {
            val name = binding.editListingNameEditText.text.toString().trim()

            if (name.isNotBlank()) {
                if (isEditMode) {
                    val listingId = arguments?.getInt(ARG_LISTING_ID)
                    listingId?.let { id -> viewModel.editListing(id, name) }
                } else {
                    viewModel.addListing(name)
                }
                dismiss()
            } else {
                binding.editListingNameEditText.error = getString(R.string.name_cannot_be_empty)
            }
        }
    }
}
