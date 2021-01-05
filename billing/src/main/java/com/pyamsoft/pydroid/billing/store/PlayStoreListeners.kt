package com.pyamsoft.pydroid.billing.store

import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsResponseListener

// https://stackoverflow.com/questions/65180072/android-billing-client-causes-memory-leak
internal interface PlayStoreListeners :
    BillingClientStateListener,
    SkuDetailsResponseListener,
    ConsumeResponseListener,
    PurchasesUpdatedListener