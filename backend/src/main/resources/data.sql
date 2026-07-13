INSERT INTO users (username, email, password, created_at)
VALUES ('guest', 'guest@webshop.tui', '$2a$10$FZ9Xh.Hbf5fw55pY3KUuGejW2xYbT6EAuJBiffign27CrokimWqYK', NOW())
ON CONFLICT (username) DO NOTHING;
