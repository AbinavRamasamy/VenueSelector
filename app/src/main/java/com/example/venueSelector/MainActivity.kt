package com.example.venueSelector

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay


@Composable
fun AvailableVenuesScreen(allVenues: List<Venue>, venuesSelected: MutableList<String>,
                          namesList: MutableList<String>, locationsList: MutableList<String>,
                          phoneNumbersList: MutableList<String>, onRegisteringClick: (Venue) -> Unit) {
    val peopleRegIS = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopCenter)
                                    .windowInsetsPadding(WindowInsets.statusBars)) {
            val visibilityStates = remember { allVenues.map { mutableStateOf(false) } }
            LaunchedEffect(Unit) {
                visibilityStates.forEachIndexed { index, state ->
                    delay(index * 100L)
                    state.value = true
                }
            }

            Text(AVAILABLE_VENUES, fontSize = 52.sp, fontFamily = FontFamily.SansSerif, textAlign = TextAlign.Center,
                modifier = Modifier.padding(0.dp, 12.dp).align(Alignment.CenterHorizontally))

            GenList(visibilityStates, allVenues, onRegisteringClick, venuesSelected,
                    namesList, locationsList, phoneNumbersList)
        }
    }
}

@Composable
fun RegisteringScreen(venuesSelected: MutableList<String>, namesList: MutableList<String>,
                      locationsList: MutableList<String>, phoneNumbersList: MutableList<String>,
                      venue: Venue, onAvailableClick: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var names = remember { mutableStateListOf("") }
    var locations = remember { mutableStateListOf("") }
    var phoneNumbers = remember { mutableStateListOf("") }

    var times by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopCenter)
                                    .windowInsetsPadding(WindowInsets.statusBars)) {
            Text(REGISTERING, fontSize = 56.sp, fontFamily = FontFamily.SansSerif, textAlign = TextAlign.Center,
                modifier = Modifier.padding(0.dp, 12.dp).fillMaxWidth())


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onAvailableClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified)) {
                    Text(BACK_TO_AVAILABLE_VENUES, fontSize = 14.sp, fontFamily = FontFamily.Serif)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                    enabled = (names[0].isNotBlank() && locations[0].isNotBlank() &&
                            phoneNumbers.all { PHONE_NUMBER_REGEX.matches(it) } &&
                            !phoneNumbers.any { it in phoneNumbersList } &&
                            (phoneNumbers.size == phoneNumbers.toSet().size)),
                    onClick = {
                        Log.d(REGISTERING, "Registering Venue at ${venue.location_link}")
                        Log.d(REGISTERING, "Registering Venue for " +
                                                        "$names, $locations, $phoneNumbers")
                        phoneNumbers.forEachIndexed { index, phoneNumber ->
                            venuesSelected.add(venue.location_link)
                            phoneNumbersList.add(phoneNumber)
                            namesList.add(names[index])
                            locationsList.add(locations[index])
                        }

                        saveList(prefs, "venues", venuesSelected)
                        saveList(prefs, "phones", phoneNumbersList)
                        saveList(prefs, "names", namesList)
                        saveList(prefs, "locations", locationsList)

                        onAvailableClick()
                        Toast.makeText(context, REGISTER_SUCCESSFUL,
                                        Toast.LENGTH_SHORT).show()
                    }
                ) { Text(FINISH_REGISTERING, fontSize = 14.sp, fontFamily = FontFamily.Serif) }
            }

            val bottomDP = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
            Card(modifier = Modifier.padding(8.dp).fillMaxSize(),
                shape = RoundedCornerShape(16.dp, 16.dp,
                                            bottomDP, bottomDP),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)
                                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    for (i in 0..times) {
                        if (!PHONE_NUMBER_REGEX.matches(phoneNumbers[i]))
                            Text(text = PHONE_NUMBER_REQ1, color = Color.Red, fontSize = 12.sp, fontFamily = FontFamily.Serif)
                        if (phoneNumbers[i] in phoneNumbersList)
                            Text(text = PHONE_NUMBER_REQ2, color = Color.Red, fontSize = 12.sp, fontFamily = FontFamily.Serif)
                        if (phoneNumbers[i] != "" && (phoneNumbers.lastIndexOf(phoneNumbers[i]) !=
                                                        phoneNumbers.indexOf(phoneNumbers[i])))
                            Text(text = PHONE_NUMBER_REQ3, color = Color.Red, fontSize = 12.sp, fontFamily = FontFamily.Serif)
                        OutlinedTextField(
                            value = phoneNumbers[i], onValueChange = { phoneNumbers[i] = it },
                            label = { Text(PHONE_NUMBER_INPUT, fontFamily = FontFamily.Serif) },
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 4.dp))

                        if (locations[i].isBlank())
                            Text(text = LOCATION_REQ, color = Color.Red, fontSize = 12.sp, fontFamily = FontFamily.Serif)
                        OutlinedTextField(
                            value = locations[i], onValueChange = { locations[i] = it },
                            label = { Text(LOCATION_INPUT, fontFamily = FontFamily.Serif) },
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 4.dp))

                        if (names[i].isBlank())
                            Text(text = NAME_REQ, color = Color.Red, fontSize = 12.sp, fontFamily = FontFamily.Serif)
                        OutlinedTextField(
                            value = names[i], onValueChange = { names[i] = it },
                            label = { Text(NAME_INPUT, fontFamily = FontFamily.Serif) },
                            modifier = Modifier.fillMaxWidth().padding(16.dp, 4.dp,
                                                                    16.dp, 12.dp))

                        if (i != times)
                            HorizontalDivider(modifier = Modifier.padding(8.dp),
                                            DividerDefaults.Thickness, DividerDefaults.color)
                        else
                            Button(onClick = {
                                    ++times
                                    phoneNumbers.add("")
                                    locations.add("")
                                    names.add("")
                                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                                   modifier = Modifier.wrapContentSize()) {
                                Text("+", fontSize = 28.sp, fontFamily = FontFamily.Serif)
                            }
                    }
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            val allVenues = remember { VenueWrapper.loadVenuesFromJson(context) }
            var venuesSelected = loadList(prefs, "venues").toMutableStateList()
            var namesList = loadList(prefs, "names").toMutableStateList()
            var locationsList = loadList(prefs, "locations").toMutableStateList()
            var phoneNumbersList = loadList(prefs, "phones").toMutableStateList()

            var currentScreen by remember { mutableStateOf<ScreenViewState>(
                                            ScreenViewState.AvailableVenues) }

//            FOR DEBUGGING
//            saveList(prefs,"names", listOf())
//            saveList(prefs,"locations", listOf())
//            saveList(prefs,"phones", listOf())

            when (currentScreen) {
                is ScreenViewState.AvailableVenues -> {
                    AvailableVenuesScreen(allVenues = allVenues, phoneNumbersList = phoneNumbersList,
                        onRegisteringClick = { venue -> currentScreen = ScreenViewState.Registering(venue) },
                        namesList = namesList, locationsList = locationsList, venuesSelected = venuesSelected
                    )
                    Log.d(LOADING, "Loading the AvailableVenues screen")
                    Log.d(AVAILABLE_VENUES + ": allVenues", "These are all the venues: $allVenues")
                }
                is ScreenViewState.Registering -> {
                    val venue = (currentScreen as ScreenViewState.Registering).venue
                    RegisteringScreen(venue = venue, venuesSelected = venuesSelected,
                        phoneNumbersList = phoneNumbersList, namesList = namesList,
                        locationsList = locationsList, onAvailableClick =
                            { currentScreen = ScreenViewState.AvailableVenues }
                    )
                    Log.d(LOADING, "Loading the Registering screen")
                    Log.d(REGISTERING, "Currently registering: ${venue}")
                }
            }
        }
    }
}
