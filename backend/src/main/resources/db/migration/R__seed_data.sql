INSERT INTO products (name, description, price, category, image_url, stock, rating) VALUES
('Mechanical Keyboard MK-750', 'Premium mechanical keyboard with Cherry MX Blue switches, RGB backlighting, and aircraft-grade aluminum frame.', 149.99, 'peripherals', 'https://placehold.co/400x400/0a0e14/00ff41?text=MK-750', 23, 4.7),
('Ultrawide Monitor 34"', '34-inch curved ultrawide monitor with 3440x1440 resolution, 144Hz refresh rate, and HDR400 support.', 599.99, 'displays', 'https://placehold.co/400x400/0a0e14/7dd3fc?text=UW34', 8, 4.9),
('Wireless Mouse Pro', 'Ergonomic wireless mouse with 16000 DPI optical sensor, 70h battery life, and customizable side buttons.', 79.99, 'peripherals', 'https://placehold.co/400x400/0a0e14/ffb000?text=Mouse+Pro', 45, 4.5),
('USB-C Hub 7-in-1', 'Compact USB-C hub with HDMI 4K output, 3x USB-A 3.0, SD card reader, and 100W pass-through charging.', 39.99, 'accessories', 'https://placehold.co/400x400/0a0e14/ff6bcb?text=USB-C+Hub', 120, 4.3),
('Noise-Cancelling Headphones', 'Over-ear wireless headphones with active noise cancelling, 30h battery, hi-res audio support.', 249.99, 'audio', 'https://placehold.co/400x400/0a0e14/00ff41?text=ANC+HP', 15, 4.8),
('Webcam 4K Stream', '4K webcam with auto-focus, built-in ring light, dual noise-cancelling microphones, and privacy shutter.', 129.99, 'accessories', 'https://placehold.co/400x400/0a0e14/7dd3fc?text=4K+Cam', 32, 4.4),
('Desk Mat XL', 'Extra-large 900x400mm desk mat with smooth microfiber surface, non-slip rubber base, and stitched edges.', 29.99, 'accessories', 'https://placehold.co/400x400/0a0e14/ffb000?text=Desk+Mat', 80, 4.6),
('Portable SSD 2TB', 'External SSD with 2TB capacity, USB 3.2 Gen2, up to 1050MB/s read speeds, shock-resistant.', 179.99, 'storage', 'https://placehold.co/400x400/0a0e14/ff6bcb?text=SSD+2TB', 27, 4.7),
('Smart Power Strip', 'WiFi-enabled power strip with 4 AC outlets, 4 USB ports, energy monitoring, and voice assistant support.', 44.99, 'accessories', 'https://placehold.co/400x400/0a0e14/00ff41?text=Smart+Strip', 60, 4.2),
('Monitor Light Bar', 'LED monitor light bar with adjustable color temperature, touch dimmer, and space-saving asymmetric design.', 49.99, 'accessories', 'https://placehold.co/400x400/0a0e14/7dd3fc?text=Light+Bar', 55, 4.5)
ON CONFLICT DO NOTHING;

INSERT INTO users (username, email, password, created_at) VALUES
('guest', 'guest@webshop.tui', '$2a$10$0jLyyJSrznBCvw2y2AtOUemeMGd2Sd9zR/tMLKi1YpaiqYRdzm1vq', NOW())
ON CONFLICT (username) DO NOTHING;
