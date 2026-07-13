import { CartItem } from './cart-item.model';

export type OrderStatus = 'pending' | 'processing' | 'shipped' | 'delivered';

export interface Order {
  id: string;
  userId: number;
  items: CartItem[];
  total: number;
  status: OrderStatus;
  createdAt: Date;
}
