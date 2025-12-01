INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    (1, 'The Lost Kingdom', 'A. Writer', 'ISBN-001', 199.99,
     'Adventure in a forgotten land', 'lost-kingdom.jpg', FALSE),

    (2, 'Quantum Universe', 'Dr. S. Hawkins', 'ISBN-002', 249.50,
     'Deep dive into quantum mechanics explained simply', 'quantum-universe.jpg', FALSE),

    (3, 'Ancient Empires', 'H. Roberts', 'ISBN-003', 175.00,
     'Exploring the greatest civilizations in history', 'ancient-empires.jpg', FALSE),

    (4, 'Modern Programming', 'J. Developer', 'ISBN-004', 289.00,
     'Comprehensive guide to modern programming practices', 'modern-programming.jpg', FALSE),

    (5, 'Startup Strategy', 'E. Johnson', 'ISBN-005', 210.00,
     'Guide for launching and scaling a startup', 'startup-strategy.jpg', FALSE);


INSERT INTO books_categories (book_id, category_id)
VALUES
    (1, 1),  -- Fiction
    (2, 2),  -- Science
    (3, 3),  -- History
    (4, 4),  -- Technology
    (5, 5),  -- Business
    (4, 2),  -- Programming book also relates to scientific topics
    (5, 4);  -- Startup book may also sit in Technology