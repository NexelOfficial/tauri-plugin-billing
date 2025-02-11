use serde::{Deserialize, Serialize};

/// Request payload with product_id
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchaseRequest {
    /// The product id of the product to purchase.
    pub product_id: String
}

/// Response payload for initiating a purchase.
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchaseResponse {
    /// Wether the purchase was succesfull or not
    pub success: bool,
}

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct Product {
    pub description: String,
    pub name: String,
    pub product_id: String,
    pub product_type: String,
    pub title: String,
    pub price: String,
}

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct Purchase {
    pub developer_payload: String,
    pub order_id: String,
    pub original_json: String,
    pub package_name: String,
    pub products: Vec<String>,
    pub purchase_state: String,
    pub purchase_time: i128,
    pub purchase_token: String,
    pub quantity: i32,
    pub signature: String,
    pub is_acknowledged: bool,
    pub is_auto_renewing: bool
}

/// Response payload for getting product details
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ProductsResponse {
    /// A list of products
    pub products: Vec<Product>
}

/// Response payload for getting all purchases
#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchasesResponse {
    /// A list of purchases
    pub purchases: Vec<Purchase>
}
