import { Injectable, signal, computed } from '@angular/core';
import { Product } from '../models';

const MOCK_PRODUCTS: Product[] = [
  {
    id: 1,
    name: 'Mechanical Keyboard MK-750',
    description:
      'Hot-swappable mechanical keyboard with RGB backlighting. Cherry MX switches, aluminum frame, USB-C connectivity.',
    price: 149.99,
    category: 'peripherals',
    image: 'https://placehold.co/400x300/0a0e14/00ff41?text=MK-750',
    stock: 23,
    rating: 4.7,
  },
  {
    id: 2,
    name: 'Ultrawide Monitor 34"',
    description:
      '34-inch curved ultrawide QHD monitor. 144Hz refresh rate, 1ms response time, HDR400, USB-C passthrough.',
    price: 599.99,
    category: 'displays',
    image: 'https://placehold.co/400x300/0a0e14/7dd3fc?text=UW-34',
    stock: 8,
    rating: 4.9,
  },
  {
    id: 3,
    name: 'Wireless Mouse Pro',
    description:
      'Ergonomic wireless mouse with 25K DPI sensor. 70-hour battery, Bluetooth + 2.4GHz dual connectivity.',
    price: 79.99,
    category: 'peripherals',
    image: 'https://placehold.co/400x300/0a0e14/ffb000?text=WM-PRO',
    stock: 45,
    rating: 4.5,
  },
  {
    id: 4,
    name: 'USB-C Hub 7-in-1',
    description:
      'USB-C hub with HDMI 4K, 3x USB-A 3.0, SD card reader, microSD, and 100W PD pass-through.',
    price: 39.99,
    category: 'accessories',
    image: 'https://placehold.co/400x300/0a0e14/ff6bcb?text=USB-C',
    stock: 120,
    rating: 4.3,
  },
  {
    id: 5,
    name: 'Noise-Cancelling Headphones',
    description:
      'Over-ear ANC headphones with 40-hour battery. Hi-Res Audio certified, multipoint Bluetooth 5.3.',
    price: 249.99,
    category: 'audio',
    image: 'https://placehold.co/400x300/0a0e14/e6e6e9?text=ANC-40',
    stock: 15,
    rating: 4.8,
  },
  {
    id: 6,
    name: 'Webcam 4K Stream',
    description:
      '4K60 webcam with auto-focus, built-in ring light, noise-cancelling microphone, privacy shutter.',
    price: 129.99,
    category: 'accessories',
    image: 'https://placehold.co/400x300/0a0e14/00ff41?text=WC-4K',
    stock: 32,
    rating: 4.4,
  },
  {
    id: 7,
    name: 'Desk Mat XL',
    description:
      '900x400mm desk mat, premium micro-weave surface, stitched edges, non-slip rubber base.',
    price: 29.99,
    category: 'accessories',
    image: 'https://placehold.co/400x300/0a0e14/7dd3fc?text=DSK-XL',
    stock: 80,
    rating: 4.6,
  },
  {
    id: 8,
    name: 'Portable SSD 2TB',
    description:
      'External SSD with 2000MB/s read speeds. IP65 water/dust resistant, hardware encryption, USB 3.2.',
    price: 179.99,
    category: 'storage',
    image: 'https://placehold.co/400x300/0a0e14/ffb000?text=SSD-2T',
    stock: 27,
    rating: 4.7,
  },
  {
    id: 9,
    name: 'Smart Power Strip',
    description:
      'Wi-Fi enabled power strip with 4 AC outlets + 4 USB ports. Individual outlet control, energy monitoring.',
    price: 44.99,
    category: 'accessories',
    image: 'https://placehold.co/400x300/0a0e14/ff6bcb?text=PWR-STR',
    stock: 60,
    rating: 4.2,
  },
  {
    id: 10,
    name: 'Monitor Light Bar',
    description:
      'LED monitor light bar with auto-dimming, color temperature 2700K-6500K, no screen glare, USB powered.',
    price: 49.99,
    category: 'accessories',
    image: 'https://placehold.co/400x300/0a0e14/e6e6e9?text=LGT-BAR',
    stock: 55,
    rating: 4.5,
  },
];

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly products = signal<Product[]>(MOCK_PRODUCTS);

  readonly allProducts = this.products.asReadonly();

  readonly categories = computed(() => {
    const cats = new Set(this.products().map((p) => p.category));
    return Array.from(cats);
  });

  getProductById(id: number): Product | undefined {
    return this.products().find((p) => p.id === id);
  }

  getByCategory(category: string): Product[] {
    return this.products().filter((p) => p.category === category);
  }

  search(query: string): Product[] {
    const q = query.toLowerCase();
    return this.products().filter(
      (p) =>
        p.name.toLowerCase().includes(q) ||
        p.description.toLowerCase().includes(q) ||
        p.category.toLowerCase().includes(q),
    );
  }
}
