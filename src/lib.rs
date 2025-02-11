use tauri::{
    plugin::{Builder, TauriPlugin},
    Manager, Runtime,
};

pub use models::*;

#[cfg(mobile)]
mod mobile;

mod commands;
mod error;
mod models;

pub use error::{Error, Result};

#[cfg(mobile)]
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
            commands::initiate_purchase,
            commands::get_price
        ])
        .setup(|app, api| {
            #[cfg(mobile)]
            let billing = mobile::init(app, api)?;
            app.manage(billing);
            Ok(())
        })
        .build()
}
