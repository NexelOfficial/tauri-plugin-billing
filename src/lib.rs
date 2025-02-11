#![cfg(mobile)]

use tauri::{
    plugin::{Builder, TauriPlugin},
    Manager, Runtime,
};

pub use models::*;

mod mobile;

mod commands;
mod error;
mod models;

pub use error::{Error, Result};

use mobile::Billing;

/// Extensions to [`tauri::App`], [`tauri::AppHandle`] and [`tauri::Window`] to access the billing APIs.
pub trait BillingExt<R: Runtime> {
    fn billing(&self) -> &Billing<R>;
}

impl<R: Runtime, T: Manager<R>> crate::BillingExt<R> for T {
    fn billing(&self) -> &Billing<R> {
        self.state::<Billing<R>>().inner()
    }
}

/// Initializes the plugin.
pub fn init<R: Runtime>() -> TauriPlugin<R> {
    Builder::new("billing")
        .invoke_handler(tauri::generate_handler![
            commands::create_purchase,
            commands::get_product,
            commands::get_all_purchases,
            commands::consume
        ])
        .setup(|app, api| {
            let billing = mobile::init(app, api)?;
            app.manage(billing);
            Ok(())
        })
        .build()
}
