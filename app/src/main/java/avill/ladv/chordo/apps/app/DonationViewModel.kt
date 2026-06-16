package avill.ladv.chordo.apps.app

import android.app.Activity
import androidx.lifecycle.ViewModel
import avill.ladv.chordo.util.BillingHelper
import com.android.billingclient.api.ProductDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DonationViewModel @Inject constructor(
    private val billingHelper: BillingHelper
) : ViewModel() {

    val products: StateFlow<List<ProductDetails>> = billingHelper.products
    val isBillingReady: StateFlow<Boolean> = billingHelper.isBillingReady
    val purchaseSuccessEvent: SharedFlow<String> = billingHelper.purchaseSuccessEvent

    fun donate(activity: Activity, productDetails: ProductDetails) {
        billingHelper.launchBillingFlow(activity, productDetails)
    }

    fun refreshProducts() {
        billingHelper.startConnection()
    }
}
