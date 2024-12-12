package com.eldroid.listings.utils

import com.eldroid.listings.model.AddListingResponse
import com.eldroid.listings.model.DeleteResponse
import com.eldroid.listings.model.Listing
import com.eldroid.listings.model.UpdateListingResponse
import retrofit2.Response
import retrofit2.http.*

interface ListingsApi {
    @GET("lists")
    suspend fun getListings(): Response<List<Listing>>

    @POST("lists")
    suspend fun addListing(@Body listing: Listing): Response<AddListingResponse>

    @PATCH("lists/{id}")
    suspend fun updateListing(
        @Path("id") id: Int,
        @Body listing: Listing
    ): Response<UpdateListingResponse>

    @DELETE("lists/{id}")
    suspend fun deleteListing(@Path("id") id: Int): Response<DeleteResponse>
}
