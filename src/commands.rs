use tauri::{command, AppHandle, Runtime};

use crate::models::*;
use crate::BillingExt;
use crate::Result;

#[command]
pub(crate) async fn initiate_purchase<R: Runtime>(
    app: AppHandle<R>,
    payload: PurchaseRequest,
) -> Result<PurchaseResponse> {
    app.billing().initiate_purchase(payload)
}

#[command]
pub(crate) async fn get_price<R: Runtime>(
    app: AppHandle<R>,
    payload: PurchaseRequest,
) -> Result<PriceResponse> {
    app.billing().get_price(payload)
}