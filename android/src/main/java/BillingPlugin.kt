package com.plugin.billing

import android.app.Activity
import android.webkit.WebView
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin
import app.tauri.plugin.Invoke
import com.android.billingclient.api.*

@InvokeArg
class PurchaseArgs {
    var sku: String? = null // SKU of the product to purchase
}

@TauriPlugin
class BillingPlugin(private val activity: Activity) : Plugin(activity) {
    private lateinit var billingClient: BillingClient

    override fun load(webView: WebView) {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                // Handle purchase updates here
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection
            }
        })
    }

    @Command
    fun initiatePurchase(invoke: Invoke) {
        val args = invoke.parseArgs(PurchaseArgs::class.java)
        val sku = args.sku ?: run {
            invoke.reject("SKU is required")
            return
        }

        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(sku))
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    val billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()

                    val responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).responseCode
                    if (responseCode == BillingClient.BillingResponseCode.OK) {
                        val ret = JSObject()
                        ret.put("status", "Purchase flow started")
                        invoke.resolve(ret)
                    } else {
                        invoke.reject("Failed to start purchase flow")
                    }
                    return@querySkuDetailsAsync
                }
                invoke.reject("SKU not found")
            } else {
                invoke.reject("Failed to query SKU details")
            }
        }
    }

    @Command
    fun getPrice(invoke: Invoke) {
        val args = invoke.parseArgs(PurchaseArgs::class.java)
        val sku = args.sku ?: run {
            invoke.reject("SKU is required")
            return
        }

        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(sku))
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    val ret = JSObject()
                    ret.put("price", skuDetails.price)
                    invoke.resolve(ret)
                    return@querySkuDetailsAsync
                }
                invoke.reject("SKU not found")
            } else {
                invoke.reject("Failed to query SKU details")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Handle the purchase (e.g., validate and acknowledge it)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Purchase acknowledged
                    }
                }
            }
        }
    }
}