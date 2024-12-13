package com.eldroid.listings.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.eldroid.listings.R
import com.eldroid.listings.databinding.ActivityMainBinding
import com.eldroid.listings.viewmodel.ListingsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ListingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        viewModel = ViewModelProvider(this)[ListingsViewModel::class.java]

        
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add -> {
                    showAddListingDialog()
                    true
                }
                else -> false
            }
        }

        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ListViewFragment())
                .commit()
        }
    }

    private fun showAddListingDialog() {
        val dialog = AddEditListingDialogFragment.newInstance(
            dialogTitle = getString(R.string.add_listing),
            isEditMode = false
        )
        dialog.show(supportFragmentManager, "AddListingDialog")
        dialog.isCancelable = false
    }
}
