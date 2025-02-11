![Barcode Scanner](https://raw.githubusercontent.com/NexelOfficial/tauri-plugin-billing/refs/heads/main/banner.png)

Handle one-time purchases using the Android Billing API

| Platform | Supported |
| -------- | --------- |
| Linux    | x         |
| Windows  | x         |
| macOS    | x         |
| Android  | ✓         |
| iOS      | x         |

## Install

### Automatic

> [!IMPORTANT]  
> You will need at least version v2.2.5 of @tauri-apps/cli

To install this plugin, simply run the following command:

```
npx tauri add billing
```

### Manual

1. Install the npm package:

```sh
npm add tauri-plugin-billing-api
# or
pnpm add tauri-plugin-billing-api
# or
yarn add tauri-plugin-billing-api
```

2. Install the Core plugin by adding the following to your `Cargo.toml` file:

```toml
[dependencies]
tauri-plugin-billing = "0.1"
```

3. Add the initialisation code:

```rust
fn main() {
    tauri::Builder::default()
        .plugin(tauri_plugin_billing::init())
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
```
