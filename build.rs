const COMMANDS: &[&str] = &["initiate_purchase", "get_price"];

fn main() {
  tauri_plugin::Builder::new(COMMANDS)
    .android_path("android")
    .ios_path("ios")
    .build();
}
