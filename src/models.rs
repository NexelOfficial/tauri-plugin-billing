use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchaseRequest {
    /// The product id of the product to purchase.
    pub product_id: String
}

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ConsumeRequest {
    /// The purchase token used for consume request
    pub purchase_token: String
}

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchaseResponse {
    /// Wether the purchase was succesful or not
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
    pub order_id: Option<String>,
    pub original_json: String,
    pub package_name: String,
    pub products: Vec<String>,
    pub purchase_state: i32,
    pub purchase_time: i128,
    pub purchase_token: String,
    pub quantity: i32,
    pub signature: String,
    pub is_acknowledged: bool,
    pub is_auto_renewing: bool
}

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct ProductsResponse {
    /// A list of products
    pub products: Vec<Product>
}

#[derive(Debug, Clone, Default, Deserialize, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PurchasesResponse {
    /// A list of purchases
    pub purchases: Vec<Purchase>
}
