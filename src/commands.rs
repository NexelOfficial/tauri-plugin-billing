use tauri::{command, AppHandle, Runtime};

use crate::models::*;
use crate::BillingExt;
use crate::Result;

#[command]
pub(crate) async fn create_purchase<R: Runtime>(
    app: AppHandle<R>,
    payload: PurchaseRequest,
) -> Result<PurchaseResponse> {
    app.billing().create_purchase(payload)
}

#[command]
pub(crate) async fn get_product<R: Runtime>(
    app: AppHandle<R>,
    payload: PurchaseRequest,
) -> Result<ProductsResponse> {
    app.billing().get_product(payload)
}

#[command]
pub(crate) async fn get_all_purchases<R: Runtime>(app: AppHandle<R>) -> Result<PurchasesResponse> {
    app.billing().get_all_purchases()
}
