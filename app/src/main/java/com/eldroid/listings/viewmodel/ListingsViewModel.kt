package com.eldroid.listings.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eldroid.listings.R
import com.eldroid.listings.model.Listing
import com.eldroid.listings.utils.RetrofitClient
import kotlinx.coroutines.launch

class ListingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _listings = MutableLiveData<MutableList<Listing>>()
    val listings: LiveData<MutableList<Listing>> = _listings
    private val appContext: Context = application.applicationContext

    private val api = RetrofitClient.instance

    init {
        fetchListings()
    }

    private fun fetchListings() {
        viewModelScope.launch {
            try {
                val response = api.getListings()
                if (response.isSuccessful) {
                    _listings.value = response.body()?.toMutableList() ?: mutableListOf()
                } else {
                    Toast.makeText(appContext, appContext.getString(R.string.failed_fetch), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(appContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addListing(name: String) {
        viewModelScope.launch {
            try {
                val currentListings = _listings.value ?: mutableListOf()
                val newListing = Listing(
                    id = currentListings.size + 1,
                    name = name
                )

                val response = api.addListing(newListing)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()
                        currentListings.add(newListing)
                        _listings.value = currentListings
                    }
                } else {
                    Toast.makeText(appContext, appContext.getString(R.string.failed_add), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(appContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun editListing(id: Int, newName: String) {
        viewModelScope.launch {
            try {
                val updatedListing = Listing(id, newName)
                val response = api.updateListing(id, updatedListing)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()

                        val currentList = _listings.value ?: return@launch
                        val index = currentList.indexOfFirst { it.id == id }
                        if (index != -1) {
                            currentList[index] = updatedListing
                            _listings.value = currentList
                        }
                    }
                } else {
                    Toast.makeText(appContext, appContext.getString(R.string.failed_update), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(appContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteListing(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.deleteListing(id)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(appContext, it.message, Toast.LENGTH_SHORT).show()

                        val currentList = _listings.value ?: return@launch
                        currentList.removeAt(currentList.indexOfFirst { it.id == id })
                        _listings.value = currentList
                    }
                } else {
                    Toast.makeText(appContext, appContext.getString(R.string.failed_delete), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(appContext, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
