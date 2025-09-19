package com.example.venueSelector

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImage
import kotlin.String


@Composable
fun LoadImage(venue: Venue) {
    Log.d("VenueImage", "Loading image for: $venue.shop_name at $venue.location")
    AsyncImage(
        model = "$IMAGE_BASE_URL${venue.banner_url}.png", contentScale = ContentScale.FillWidth,
        contentDescription = CONTENT_DESCRIPTION_BASE + venue.location + ", " + venue.shop_name,
        modifier = Modifier.fillMaxWidth().padding(6.dp)
    )
}

@Composable
fun GenList(visibilityStates: List<MutableState<Boolean>>, allVenues: List<Venue>,
            onRegisteringClick: (Venue) -> Unit, venuesSelected: MutableList<String>,
            namesList: MutableList<String>, locationsList: MutableList<String>,
            phoneNumbersList: MutableList<String>) {
    Log.d("GenList", "Generating all venue cards")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        itemsIndexed(allVenues) { index, loadedVenue ->
            val isVisible = (visibilityStates[index].value)


            AnimatedVisibility(visible = isVisible, enter = slideInVertically { it } + fadeIn()) {
                GetCard(loadedVenue, venuesSelected, namesList, locationsList, phoneNumbersList, onRegisteringClick)
            }
        }
    }
}
@Composable
fun GetCard(venue: Venue, venuesSelected: MutableList<String>, namesList: MutableList<String>,
            locationsList: MutableList<String>, phoneNumbersList: MutableList<String>,
            onRegisteringClick: (Venue) -> Unit) {
    Log.d("GetCard", "Generating venue cards for: $venue")

    Card(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            GetVenueDescription(venue, venuesSelected, namesList, locationsList, phoneNumbersList)
            GetVenueButtons(venue, onRegisteringClick)
        }
    }
}

@Composable
fun GetVenueDescription(venue: Venue, venuesSelected: MutableList<String>, namesList: MutableList<String>,
                        locationsList: MutableList<String>, phoneNumbersList: MutableList<String>) {
    var showDialog by remember { mutableStateOf(false) }

    Row {
        Text(venue.location, fontSize = 32.sp, fontFamily = FontFamily.Serif,
            color = Color.Black, textAlign = TextAlign.Start)

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { showDialog = true }, shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(0.dp, 0.dp, 6.dp, 0.dp)) {
            Text(text = DIALOG_TITLE, fontSize = 10.sp, textAlign = TextAlign.End, fontFamily = FontFamily.Serif)
        }
    }
    Text(venue.shop_name, fontSize = 20.sp, color = Color.Black, fontFamily = FontFamily.Serif)
    Text(venue.date + ", " + venue.time, fontSize = 18.sp, color = Color.Black, fontFamily = FontFamily.Serif)
    LoadImage(venue)
    Text(venue.address, fontSize = 14.sp, color = Color.Black, fontFamily = FontFamily.Serif)

    if (showDialog)
        MakeDialog(onDismiss = { showDialog = false }, venue, venuesSelected, namesList, locationsList, phoneNumbersList)
}
@Composable
fun GetVenueButtons(venue: Venue, onRegisteringClick: (Venue) -> Unit) {
    val context = LocalContext.current

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 0.dp),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, venue.location_link.toUri())
                Log.d(OPENING_MAPS, "Opening Google Maps for Venue at " +
                        (venue.location + ", " + venue.shop_name))
                context.startActivity(intent)
            }) {
            Text(text = OPEN_MAPS, fontSize = 12.sp, modifier = Modifier, fontFamily = FontFamily.Serif)
        }
        Button(modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 0.dp),
            onClick = { onRegisteringClick(venue) }) {
            Text(REGISTER, fontSize = 12.sp, modifier = Modifier, fontFamily = FontFamily.Serif)
        }
    }
}

@Composable
fun MakeDialog(onDismiss: () -> Unit, venue: Venue, venuesSelected: MutableList<String>,
               namesList: MutableList<String>, locationsList: MutableList<String>,
               phoneNumbersList: MutableList<String>) {
    Log.d("Dialog", "Opened a Dialog")


    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false,
            dismissOnBackPress = true, dismissOnClickOutside = true)) {
        Card(modifier = Modifier.fillMaxWidth().heightIn(max = 700.dp).padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                item { Text(DIALOG_TITLE, fontSize = 24.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 6.dp).fillMaxWidth(), fontFamily = FontFamily.Serif) }

                item { GetPeopleList(venuesSelected, phoneNumbersList, namesList, locationsList, venue) }
            }
        }
    }
}
@Composable
fun GetPeopleList(venuesSelected: MutableList<String>, phoneNumbersList: MutableList<String>,
                  namesList: MutableList<String>, locationsList: MutableList<String>, venue: Venue) {
    if (!venuesSelected.contains(venue.location_link)) {
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

        Text(NONE_REGISTERED, fontSize = 18.sp, textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp).fillMaxWidth(), fontFamily = FontFamily.Serif)
    } else
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally) {
                for (index in venuesSelected.indices)
                    if (venuesSelected[index] == venue.location_link)
                        GetPerson(venuesSelected, phoneNumbersList, namesList, locationsList, index)
            }
        }
}
@Composable
fun GetPerson(venuesSelected: MutableList<String>, phoneNumbersList: MutableList<String>,
              namesList: MutableList<String>, locationsList: MutableList<String>, index: Int) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Name: ${namesList[index]}" +
                "\n\nLocation: ${locationsList[index]}" +
                "\n\nPhone Number: ${phoneNumbersList[index]}", fontFamily = FontFamily.Serif,
            modifier = Modifier.padding(top = 6.dp).weight(.8f), textAlign = TextAlign.Start)

        Spacer(modifier = Modifier.weight(.2f))

        Button(onClick = {
            Log.d(UNREGISTERING, "Unregistering from: ${venuesSelected[index]}; " +
                    "${namesList[index]}, ${locationsList[index]}, ${phoneNumbersList[index]}")

            venuesSelected.removeAt(index)
            phoneNumbersList.removeAt(index)
            namesList.removeAt(index)
            locationsList.removeAt(index)

            saveList(prefs, "venues", (venuesSelected))
            saveList(prefs, "phones", (phoneNumbersList))
            saveList(prefs, "names", (namesList))
            saveList(prefs, "locations", (locationsList))

            Toast.makeText(context, "Successfully removed registration", Toast.LENGTH_SHORT).show() },
            shape = RoundedCornerShape(100), modifier = Modifier.wrapContentSize()) {
            Text(text = "X", fontSize = 18.sp, textAlign = TextAlign.End)
        }
    }
}
