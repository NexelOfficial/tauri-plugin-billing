use serde::{Deserialize, Serialize};

/// Request payload for initiating a purchase.
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchaseRequest {
    /// The SKU of the product to purchase.
    pub sku: String,
}

/// Response payload for initiating a purchase.
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchaseResponse {
    /// The status of the purchase flow.
    pub status: String,
}

/// Response payload for getting a price
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PriceResponse {
    /// The price of the product
    pub price: String,
}
