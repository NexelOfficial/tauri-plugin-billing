import { invoke } from "@tauri-apps/api/core";

export async function initiatePurchase(sku: string): Promise<string | null> {
  return await invoke<{ value?: string }>("plugin:billing|initiate_purchase", {
    payload: {
      sku,
    },
  }).then((r) => (r.value ? r.value : null));
}

export async function getPrice(sku: string): Promise<string | null> {
  return await invoke<{ price?: string }>("plugin:billing|get_price", {
    payload: {
      sku,
    },
  }).then((r) => (r.price ? r.price : null));
}
