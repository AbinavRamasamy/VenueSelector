package com.example.venueSelector


sealed class ScreenViewState() {
    object AvailableVenues : ScreenViewState()

    data class Registering(val venue: Venue) : ScreenViewState()
}
