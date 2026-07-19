INSERT INTO restaurants (owner_id, name, description, address, cuisine, rating, image_url) VALUES
(1, 'Biryani House', 'Authentic Hyderabadi biryani', 'Banjara Hills, Hyderabad', 'Indian', 4.5, 'https://picsum.photos/seed/biryani/400/250'),
(1, 'Pasta Palace', 'Fresh handmade pasta daily', 'Jubilee Hills, Hyderabad', 'Italian', 4.2, 'https://picsum.photos/seed/pasta/400/250'),
(1, 'Dragon Wok', 'Sizzling Indo-Chinese classics', 'Madhapur, Hyderabad', 'Chinese', 4.0, 'https://picsum.photos/seed/wok/400/250');

INSERT INTO menu_items (restaurant_id, name, description, price, category, image_url) VALUES
(1, 'Chicken Biryani', 'Dum-cooked with saffron', 299.00, 'Mains', 'https://picsum.photos/seed/cb/300/200'),
(1, 'Veg Biryani', 'Seasonal vegetables, basmati', 229.00, 'Mains', 'https://picsum.photos/seed/vb/300/200'),
(1, 'Raita', 'Cool curd with onions', 49.00, 'Sides', 'https://picsum.photos/seed/raita/300/200'),
(1, 'Gulab Jamun', 'Two pieces, warm', 79.00, 'Desserts', 'https://picsum.photos/seed/gj/300/200'),
(2, 'Margherita Pizza', 'San Marzano, basil', 349.00, 'Pizza', 'https://picsum.photos/seed/mp/300/200'),
(2, 'Alfredo Pasta', 'Creamy parmesan sauce', 329.00, 'Pasta', 'https://picsum.photos/seed/ap/300/200'),
(2, 'Garlic Bread', 'With cheese dip', 149.00, 'Sides', 'https://picsum.photos/seed/gb/300/200'),
(3, 'Hakka Noodles', 'Wok-tossed veggies', 199.00, 'Noodles', 'https://picsum.photos/seed/hn/300/200'),
(3, 'Chilli Chicken', 'Dry, spicy, addictive', 269.00, 'Starters', 'https://picsum.photos/seed/cc/300/200'),
(3, 'Fried Rice', 'Egg or veg', 189.00, 'Rice', 'https://picsum.photos/seed/fr/300/200');