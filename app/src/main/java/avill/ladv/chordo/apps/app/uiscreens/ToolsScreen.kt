package avill.ladv.chordo.apps.app.uiscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import avill.ladv.chordo.R
import avill.ladv.chordo.apps.app.DonationViewModel
import avill.ladv.chordo.apps.app.helpers.ChordFinder
import avill.ladv.chordo.apps.app.helpers.CHROMATIC_SCALE
import avill.ladv.chordo.apps.app.helpers.VARIATIONS
import android.app.Activity
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ToolsScreen(
    chordFinder: ChordFinder = remember { ChordFinder() },
    donationViewModel: DonationViewModel = hiltViewModel()
) {
    var selectedRoot by remember { mutableStateOf("C") }
    var selectedVariation by remember { mutableStateOf("major") }
    var variations by remember { mutableStateOf<List<List<Int>>>(emptyList()) }

    // Mapping from VARIATIONS keys to user-friendly suffixes that ChordFinder.parse understands
    val variationToSuffix = mapOf(
        "major" to "",
        "minor" to "m",
        "major7" to "7",
        "c5" to "5",
        "cdim" to "dim",
        "caug" to "aug",
        "csus2" to "sus2",
        "csus4" to "sus4",
        "cmaj7" to "maj7",
        "cm7" to "m7",
        "c6" to "6",
        "cm6" to "m6",
        "c9" to "9",
        "cm9" to "m9"
    )

    LaunchedEffect(selectedRoot, selectedVariation) {
        val suffix = variationToSuffix[selectedVariation] ?: ""
        variations = chordFinder.getMatrix(selectedRoot + suffix)
    }

    val products by donationViewModel.products.collectAsState()
    val isBillingReady by donationViewModel.isBillingReady.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        donationViewModel.purchaseSuccessEvent.collect {
            Toast.makeText(context, context.getString(R.string.thanks_for_support), Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.chord_finder),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Horizontal list of the CHROMATIC_SCALE
        ScaleRow(CHROMATIC_SCALE, selectedRoot) { selectedRoot = it }

        Spacer(modifier = Modifier.height(16.dp))

        // VARIATIONS list
        Text(
            text = stringResource(R.string.options),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(VARIATIONS.keys.toList()) { variation ->
                FilterChip(
                    selected = selectedVariation == variation,
                    onClick = { selectedVariation = variation },
                    label = { Text(variation.replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        // Variations display
        if (variations.isNotEmpty()) {
            Text(
                text = stringResource(R.string.chord_variations_title, selectedRoot + (variationToSuffix[selectedVariation] ?: "")),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(variations) { matrixRow ->
                    ChordDiagram(
                        matrixRow = matrixRow,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Billing at the bottom
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.support_developer),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isBillingReady) {
            if (products.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_donation_options),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(products) { product ->
                        Button(
                            onClick = { activity?.let { donationViewModel.donate(it, product) } }
                        ) {
                            Text(stringResource(R.string.product_donation_format, product.name, product.oneTimePurchaseOfferDetails?.formattedPrice ?: ""))
                        }
                    }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.connecting_to_play_store))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ScaleRow(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(items) { item ->
            FilterChip(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                label = { Text(item) }
            )
        }
    }
}
