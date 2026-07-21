export interface Order {
  id: number;
  status: string;
  total: number;
  items: OrderItem[];
  shippingAddress: string;
  shippingCity: string;
  shippingZip: string;
  createdAt: string;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface OrderRequest {
  shippingAddress: string;
  shippingCity: string;
  shippingZip: string;
}
