use serde::de::DeserializeOwned;
use tauri::{
    plugin::{PluginApi, PluginHandle},
    AppHandle, Runtime,
};

use crate::models::*;

#[cfg(target_os = "android")]
const PLUGIN_IDENTIFIER: &str = "com.plugin.billing";

#[cfg(target_os = "ios")]
tauri::ios_plugin_binding!(init_plugin_billing);

// initializes the Kotlin or Swift plugin classes
pub fn init<R: Runtime, C: DeserializeOwned>(
    _app: &AppHandle<R>,
    api: PluginApi<R, C>,
) -> crate::Result<Billing<R>> {
    #[cfg(target_os = "android")]
    let handle = api.register_android_plugin(PLUGIN_IDENTIFIER, "BillingPlugin")?;
    #[cfg(target_os = "ios")]
    let handle = api.register_ios_plugin(init_plugin_billing)?;
    Ok(Billing(handle))
}

/// Access to the billing APIs.
pub struct Billing<R: Runtime>(PluginHandle<R>);

impl<R: Runtime> Billing<R> {
    pub fn create_purchase(&self, payload: PurchaseRequest) -> crate::Result<PurchaseResponse> {
        self.0
            .run_mobile_plugin("createPurchase", payload)
            .map_err(Into::into)
    }

    pub fn get_product(&self, payload: PurchaseRequest) -> crate::Result<ProductsResponse> {
        self.0
            .run_mobile_plugin("getProduct", payload)
            .map_err(Into::into)
    }

    pub fn get_all_purchases(&self) -> crate::Result<PurchasesResponse> {
        self.0
            .run_mobile_plugin("getAllPurchases", 0)
            .map_err(Into::into)
    }
}
