package com.plugin.billing

import android.app.Activity
import android.webkit.WebView
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.JSObject
import app.tauri.plugin.JSArray
import app.tauri.plugin.Plugin
import app.tauri.plugin.Invoke
import com.android.billingclient.api.*

@InvokeArg
class PurchaseArgs {
    var productId: String? = null
}

fun getBillingMessage(billingResult: BillingResult): String {
    return when (billingResult.responseCode) {
        -3 -> "SERVICE_TIMEOUT"
        -2 -> "FEATURE_NOT_SUPPORTED"
        -1 -> "SERVICE_DISCONNECTED"
        0 -> "OK"
        1 -> "USER_CANCELED"
        2 -> "SERVICE_UNAVAILABLE"
        3 -> "BILLING_UNAVAILABLE"
        4 -> "ITEM_UNAVAILABLE"
        5 -> "DEVELOPER_ERROR"
        6 -> "ERROR"
        7 -> "ITEM_ALREADY_OWNED"
        8 -> "ITEM_NOT_OWNED"
        12 -> "NETWORK_ERROR"
        else -> "UNKNOWN_ERROR"
    }
}

fun getProductQueryParams(productId: String): QueryProductDetailsParams {
    return QueryProductDetailsParams.newBuilder()
    .setProductList(
        listOf(
            QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        )
    )
    .build()
}

@TauriPlugin
class BillingPlugin(private val activity: Activity) : Plugin(activity) {
    private lateinit var billingClient: BillingClient
    private var savedInvoke: Invoke? = null

    override fun load(webView: WebView) {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener { billingResult, purchases ->
                // Handle purchase updates here
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchase(purchases[0])
                } else {
                    savedInvoke?.reject("billingResult: " + getBillingMessage(billingResult))
                }
            }
            .enablePendingPurchases()
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

    private fun handlePurchase(purchase: Purchase) {
        // Handle the purchase (e.g., validate and acknowledge it)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
                
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        val ret = JSObject()
                        ret.put("success", true)
                        savedInvoke?.resolve(ret)
                    } else {
                        savedInvoke?.reject("acknowledgePurchase: " + getBillingMessage(billingResult))
                    }
                }
            } else {
                val ret = JSObject()
                ret.put("success", true)
                savedInvoke?.resolve(ret)
            }
        }

        savedInvoke = null
    }

    @Command
    fun createPurchase(invoke: Invoke) {
        savedInvoke = invoke

        val args = invoke.parseArgs(PurchaseArgs::class.java)
        val queryParams = getProductQueryParams(args.productId ?: "")

        billingClient.queryProductDetailsAsync(queryParams) { billingResult, productList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productList.isNotEmpty()) {
                val dets = productList[0]
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(dets)
                                .build()
                        )
                    )
                    .build()

                val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    invoke.reject("launchBillingFlow: " + getBillingMessage(billingResult))
                }

                return@queryProductDetailsAsync
            } else {
                invoke.reject("queryProductDetailsAsync: " + getBillingMessage(billingResult))
            }
        }
    }

    @Command
    fun getProduct(invoke: Invoke) {
        val args = invoke.parseArgs(PurchaseArgs::class.java)
        val queryParams = getProductQueryParams(args.productId ?: "")

        billingClient.queryProductDetailsAsync(queryParams) { billingResult, productList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productList.isNotEmpty()) {
                val ret = JSObject()
                val productsArray = JSArray()

                productList.forEach { dets ->
                    val productObj = JSObject().apply {
                        put("description", dets.description)
                        put("name", dets.name)
                        put("productId", dets.productId)
                        put("productType", dets.productType)
                        put("title", dets.title)
                        put("price", dets.oneTimePurchaseOfferDetails?.formattedPrice)
                    }

                    productsArray.put(productObj)
                }
                
                ret.put("products", productsArray)
                invoke.resolve(ret)
                return@queryProductDetailsAsync
            } else {
                invoke.reject("queryProductDetailsAsync: " + getBillingMessage(billingResult))
            }
        }
    }

    @Command
    fun getAllPurchases(invoke: Invoke) {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val ret = JSObject()
                val purchasesArray = JSArray()

                purchasesList.forEach { dets ->
                    val productsArray = JSArray()
                    dets.products.forEach { prod -> productsArray.put(prod) }

                    val purchase = JSObject().apply {
                        put("orderId", dets.orderId)
                        put("productIds", productsArray)
                        put("purchaseTime", dets.purchaseTime)
                        put("purchaseToken", dets.purchaseToken)
                        put("purchaseState", dets.purchaseState)
                    }

                    purchasesArray.put(purchase)
                }

                ret.put("purchases", purchasesArray)
                invoke.resolve(ret)
            } else {
                invoke.reject("queryPurchasesAsync: " + getBillingMessage(billingResult))
            }
        }
    }
}