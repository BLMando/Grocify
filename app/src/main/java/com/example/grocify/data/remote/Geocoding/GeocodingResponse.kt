package com.example.grocify.data.remote.Geocoding

import com.squareup.moshi.JsonClass


/**
 * Defined a parsable data class for the response from the Geocoding API.
 */
@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<Result>,
    val summary: Summary
)

data class Result(
    val address: Address,
    val entryPoints: List<EntryPoint> = emptyList(),
    val id: String,
    val matchConfidence: MatchConfidence,
    val position: PositionX,
    val score: Double,
    val type: String,
    val viewport: Viewport
)

data class Summary(
    val fuzzyLevel: Int,
    val numResults: Int,
    val offset: Int,
    val query: String,
    val queryTime: Int,
    val queryType: String,
    val totalResults: Int
)

data class Address(
    val country: String = "",
    val countryCode: String = "",
    val countryCodeISO3: String = "",
    val countrySecondarySubdivision: String = "",
    val countrySubdivision: String = "",
    val countrySubdivisionCode: String = "",
    val countrySubdivisionName: String = "",
    val freeformAddress: String = "",
    val localName: String = "",
    val municipality: String = "",
    val postalCode: String = "",
    val streetName: String = "",
    val streetNumber: String = ""
)

data class EntryPoint(
    val position: PositionX,
    val type: String
)

data class PositionX(
    val lat: Double,
    val lon: Double
)

data class BtmRightPoint(
    val lat: Double,
    val lon: Double
)

data class Viewport(
    val btmRightPoint: BtmRightPoint,
    val topLeftPoint: TopLeftPoint
)


data class TopLeftPoint(
    val lat: Double,
    val lon: Double
)

data class MatchConfidence(
    val score: Double
)