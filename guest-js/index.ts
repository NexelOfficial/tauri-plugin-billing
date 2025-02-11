import { invoke } from "@tauri-apps/api/core";

type BillingProduct = {
  description: string;
  name: string;
  productId: string;
  productType: string;
  title: string;
  price: string;
};

type BillingPurchase = {
  developerPayload: string;
  orderId: string;
  originalJson: string;
  packageName: string;
  products: string[];
  purchaseState: string;
  purchaseTime: number;
  purchaseToken: string;
  quantity: number;
  signature: string;
  isAcknowledged: boolean;
  isAutoRenewing: boolean;
};

export async function createPurchase(
  productId: string
): Promise<boolean | null> {
  return await invoke<{ success?: boolean }>("plugin:billing|create_purchase", {
    payload: {
      productId,
    },
  }).then((r) => r.success || null);
}

export async function getProduct(
  productId: string
): Promise<BillingProduct | null> {
  return await invoke<{ products: BillingProduct[] }>(
    "plugin:billing|get_product",
    {
      payload: {
        productId,
      },
    }
  ).then((r) => r.products?.[0] || null);
}

export async function getAllPurchases(): Promise<BillingPurchase[]> {
  return await invoke<{ purchases: BillingPurchase[] }>(
    "plugin:billing|get_all_purchases"
  ).then((r) => r.purchases || []);
}
