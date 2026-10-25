import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { MenuItem, Restaurant, RestaurantRequest, MenuItemRequest } from '../models/restaurant.model';

export const MOCK_RESTAURANTS: Restaurant[] = [
  {
    id: 1,
    name: 'The Burger Club & Grill',
    description: 'Artisanal brioche smash burgers, crispy chicken tenders, and loaded truffle parmesan fries.',
    address: '12th Main, HAL 2nd Stage, Indiranagar, Bengaluru',
    cuisine: 'American · Burgers · Fast Food',
    rating: 4.8,
    open: true,
    imageUrl: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=80',
    deliveryTime: '20-25 mins',
    deliveryFee: 0,
    priceForTwo: 550,
    distance: '1.8 km',
    offers: ['50% OFF up to ₹100', 'Free delivery above ₹299'],
    featured: true,
    tags: ['Best Seller', 'Gourmet', 'Fast Delivery'],
    menu: [
      {
        id: 101,
        name: 'Truffle Smash Double Cheeseburger',
        description: 'Two smashed premium tender beef/tenderloin patties, aged cheddar, black truffle aioli, caramelised onions on toasted brioche.',
        price: 349,
        category: 'Burgers',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.9,
        calories: 720
      },
      {
        id: 102,
        name: 'Crispy Nashville Hot Chicken Burger',
        description: 'Buttermilk fried chicken breast drenched in Nashville cayenne glaze with dill pickles, cool slaw and garlic ranch.',
        price: 299,
        category: 'Burgers',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1625813506062-0aeb1d7a094b?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.8,
        calories: 680
      },
      {
        id: 103,
        name: 'Smoked Portobello & Halloumi Burger',
        description: 'Grilled portobello mushroom cap, griddled Cypriot halloumi cheese, roasted red pepper chutney, baby arugula.',
        price: 289,
        category: 'Burgers',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1520072959219-c595dc870360?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.7,
        calories: 540
      },
      {
        id: 104,
        name: 'Loaded Truffle & Parmesan Fries',
        description: 'Golden skin-on hand-cut fries tossed in white truffle oil, grated 24-month Grana Padano, chopped rosemary & parsley.',
        price: 189,
        category: 'Sides',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 410
      },
      {
        id: 105,
        name: 'Smoked Jalapeno Cheese Poppers (6 pcs)',
        description: 'Crisp panko coated bites oozing with cheddar and cream cheese with smoky charred jalapenos. Served with chipotle mayo.',
        price: 199,
        category: 'Sides',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1541592106381-b31e9677c0e5?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.6,
        calories: 360
      },
      {
        id: 106,
        name: 'Salted Caramel Thick Milkshake',
        description: 'Slow-churned Madagascan vanilla gelato spun with fleur de sel caramel reduction and topped with buttery wafer crumble.',
        price: 179,
        category: 'Beverages',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.8,
        calories: 490
      }
    ]
  },
  {
    id: 2,
    name: 'Crust & Co. Woodfired Pizzeria',
    description: 'Authentic 48-hour fermented Neapolitan dough baked in an 800° woodfired oven with San Marzano tomatoes.',
    address: '80 Feet Road, 4th Block, Koramangala, Bengaluru',
    cuisine: 'Italian · Woodfired Pizza · Pasta',
    rating: 4.9,
    open: true,
    imageUrl: 'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=900&q=80',
    deliveryTime: '25-30 mins',
    deliveryFee: 0,
    priceForTwo: 700,
    distance: '2.4 km',
    offers: ['FLAT ₹125 OFF', 'Free garlic knots on ₹499+'],
    featured: true,
    tags: ['Woodfired', 'Award Winning', 'Authentic Italian'],
    menu: [
      {
        id: 201,
        name: 'Classic Margherita D.O.P.',
        description: 'San Marzano D.O.P tomato sauce, fresh creamy fior di latte mozzarella, aromatic Genovese basil, cold-pressed olive oil.',
        price: 429,
        category: 'Pizzas',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1604382355076-af4b0eb60143?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 680
      },
      {
        id: 202,
        name: 'Spicy Pepperoni & Hot Honey',
        description: 'Aged pork/beef pepperoni cups crisped to perfection, fresh mozzarella, drizzled with house habanero wildflower hot honey.',
        price: 549,
        category: 'Pizzas',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 5.0,
        calories: 840
      },
      {
        id: 203,
        name: 'Wild Forest Truffle & 4 Cheese',
        description: 'Creamy white base, taleggio, gorgonzola, smoked provolone, sautéed porcini mushrooms, finished with fresh thyme and white truffle oil.',
        price: 529,
        category: 'Pizzas',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.8,
        calories: 780
      },
      {
        id: 204,
        name: 'Penne Creamy Pesto Genovese',
        description: 'Al dente penne rigate enveloped in stone-ground basil pine nut pesto, toasted almonds, sun-dried tomatoes, and shaved parmigiano.',
        price: 389,
        category: 'Pasta',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1621996346565-e3d5d6281691?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.7,
        calories: 590
      },
      {
        id: 205,
        name: 'Cheesy Garlic Pull-Apart Bread',
        description: 'Fluffy woodfired dough loaf slathered with roasted garlic butter, parsley, and overflowing with melted mozzarella strings.',
        price: 219,
        category: 'Starters',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1619895092538-128341789043?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 480
      },
      {
        id: 206,
        name: 'Classic Venetian Tiramisu',
        description: 'Savoiardi ladyfingers soaked in dark roast espresso & Kahlua, layered with whipped mascarpone zabaione and Valrhona cocoa dust.',
        price: 249,
        category: 'Desserts',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 390
      }
    ]
  },
  {
    id: 3,
    name: 'Royal Dawat Biryani & Kebabs',
    description: 'Nawabi dum pukht biryanis cooked in sealed copper handis over slow burning charcoal with fragrant saffron.',
    address: 'Church Street, Off MG Road, Bengaluru',
    cuisine: 'Mughlai · Biryani · North Indian · Kebabs',
    rating: 4.7,
    open: true,
    imageUrl: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=900&q=80',
    deliveryTime: '30-35 mins',
    deliveryFee: 25,
    priceForTwo: 600,
    distance: '3.1 km',
    offers: ['20% OFF up to ₹150', 'Complimentary Gulab Jamun'],
    featured: true,
    tags: ['Royal Taste', 'Dum Pukht', 'Rich Heritage'],
    menu: [
      {
        id: 301,
        name: 'Hyderabadi Shahi Chicken Dum Biryani',
        description: 'Succulent chicken marinated overnight in 21 secret Nizam spices, layered with aged basmati rice, pure desi ghee and saffron. Served with mirchi ka salan and burani raita.',
        price: 369,
        category: 'Biryani',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.9,
        calories: 780
      },
      {
        id: 302,
        name: 'Lucknowi Mutton Gosht Biryani',
        description: 'Melt-in-mouth tender goat meat pieces gently braised in aromatic yakhni stock and slow dum cooked with long-grain basmati.',
        price: 459,
        category: 'Biryani',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.9,
        calories: 890
      },
      {
        id: 303,
        name: 'Paneer Tikka Zaffrani Dum Biryani',
        description: 'Soft tandoor roasted malai paneer cubes tossed in rich saffron curd marinade, slow cooked with fragrant basmati & mint leaves.',
        price: 319,
        category: 'Biryani',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1645177628172-a94c1f96e6db?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.6,
        calories: 640
      },
      {
        id: 304,
        name: 'Galouti Kebab with Taftan (4 pcs)',
        description: 'Legendary melt-in-the-mouth smoked minced lamb/veggie patties infused with rose water and raw papaya, served on mini saffron rotis.',
        price: 329,
        category: 'Kebabs',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.8,
        calories: 510
      },
      {
        id: 305,
        name: 'Kesari Angoori Gulab Jamun (2 pcs)',
        description: 'Warm fried khoya dumplings soaked in rose and green cardamom scented saffron syrup, garnished with silver vark and crushed pistachios.',
        price: 119,
        category: 'Desserts',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 280
      }
    ]
  },
  {
    id: 4,
    name: 'Sakura Japanese & Sushi Bar',
    description: 'Fresh Atlantic sashimi, fiery dragon maki rolls, steaming ramen bowls, and crispy prawn tempura.',
    address: 'Lavelle Road, Shanthala Nagar, Bengaluru',
    cuisine: 'Japanese · Sushi · Asian · Ramen',
    rating: 4.9,
    open: true,
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=900&q=80',
    deliveryTime: '25-35 mins',
    deliveryFee: 0,
    priceForTwo: 950,
    distance: '3.6 km',
    offers: ['15% OFF on Sushi Platters', 'Free Edamame on ₹800+'],
    featured: true,
    tags: ['Chef Curated', 'Fresh Fish', 'Authentic Japanese'],
    menu: [
      {
        id: 401,
        name: 'Volcano Spicy Salmon Crunch Roll (8 pcs)',
        description: 'Fresh Atlantic salmon, ripe avocado, cucumber, rolled and topped with spicy tobiko crab crunch, unagi glaze and sriracha kewpie.',
        price: 589,
        category: 'Sushi Rolls',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 5.0,
        calories: 420
      },
      {
        id: 402,
        name: 'Crispy Tempura Asparagus & Truffle Maki (8 pcs)',
        description: 'Crispy fried tender asparagus spears, cucumber, cream cheese, draped with avocado slices and fragrant white truffle mayonnaise.',
        price: 449,
        category: 'Sushi Rolls',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1611143669185-af224c5e3252?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.8,
        calories: 360
      },
      {
        id: 403,
        name: 'Tonkotsu / Rich Tori Ramen',
        description: '18-hour slow simmered silky broth, springy wheat noodles, soft-boiled ajitsuke tamago egg, chashu slices, menma bamboo and scallions.',
        price: 499,
        category: 'Ramen & Bowls',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.9,
        calories: 620
      },
      {
        id: 404,
        name: 'Steamed Edamame with Maldon Truffle Sea Salt',
        description: 'Young organic soy beans steamed in their pods and tossed with flaky Maldon smoked sea salt and toasted sesame oil.',
        price: 249,
        category: 'Starters',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.7,
        calories: 140
      }
    ]
  },
  {
    id: 5,
    name: 'Verde Green & Grain Bowls',
    description: 'Wholesome organic Mediterranean salad bowls, acai energy breakfast bowls, and cold-pressed citrus cleanses.',
    address: '100ft Road, Defence Colony, Indiranagar, Bengaluru',
    cuisine: 'Healthy · Salads · Vegan · Smoothie Bowls',
    rating: 4.8,
    open: true,
    imageUrl: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=900&q=80',
    deliveryTime: '15-20 mins',
    deliveryFee: 0,
    priceForTwo: 500,
    distance: '1.2 km',
    offers: ['30% OFF with code HEALTH30', 'Free Chia Pudding'],
    featured: false,
    tags: ['Pure Health', 'High Protein', 'Vegan Friendly'],
    menu: [
      {
        id: 501,
        name: 'Mediterranean Grilled Falafel & Quinoa Bowl',
        description: 'Crispy herbed chickpea falafels, fluffy tricolor quinoa, roasted paprika chickpeas, beetroot hummus, kalamata olives, tahini lemon dressing.',
        price: 349,
        category: 'Bowls',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.8,
        calories: 460
      },
      {
        id: 502,
        name: 'Smoked Salmon & Avocado Superfood Salad',
        description: 'Norwegian smoked salmon, hass avocado chunks, baby spinach, edamame, toasted pumpkin seeds, cherry tomatoes, and citrus yuzu vinaigrette.',
        price: 429,
        category: 'Bowls',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?auto=format&fit=crop&w=700&q=80',
        isVeg: false,
        isBestseller: true,
        rating: 4.9,
        calories: 480
      },
      {
        id: 503,
        name: 'Pure Amazon Acai Bliss Bowl',
        description: 'Organic wild acai berry blend topped with crunchy coconut-almond granola, dragon fruit stars, blueberries, chia seeds & raw honey.',
        price: 299,
        category: 'Smoothie Bowls',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1590301157890-4810ed352733?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 340
      }
    ]
  },
  {
    id: 6,
    name: 'Sweet Tooth Artisan Bakery & Cafe',
    description: 'Flaky double-butter French croissants, Belgian chocolate fudge brownies, and specialty pour-over coffees.',
    address: 'Sadashivanagar, Vyalikaval, Bengaluru',
    cuisine: 'Bakery · Desserts · Coffee · Breakfast',
    rating: 4.9,
    open: true,
    imageUrl: 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=900&q=80',
    deliveryTime: '15-20 mins',
    deliveryFee: 15,
    priceForTwo: 450,
    distance: '2.1 km',
    offers: ['BUY 1 GET 1 on Croissants', 'Flat ₹50 OFF on Coffee'],
    featured: false,
    tags: ['French Bakes', 'Artisan Coffee', 'Sweet Tooth'],
    menu: [
      {
        id: 601,
        name: 'Almond Frangipane Flaky Croissant',
        description: 'Golden laminated French butter croissant generously filled with rich almond cream, topped with toasted sliced almonds & snow sugar.',
        price: 199,
        category: 'Bakery',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 4.9,
        calories: 380
      },
      {
        id: 602,
        name: 'Warm Belgian Chocolate Lava Cake',
        description: 'Decadent dark chocolate molten sponge with a river of warm gooey Callebaut fudge core, served with fresh raspberry coulis.',
        price: 229,
        category: 'Desserts',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: true,
        rating: 5.0,
        calories: 490
      },
      {
        id: 603,
        name: 'Iced Spanish Latte (Condensed Milk & Espresso)',
        description: 'Double shot of high-elevation Arabica espresso poured over chilled milk, dulce de leche condensed milk and ice.',
        price: 189,
        category: 'Beverages',
        available: true,
        imageUrl: 'https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?auto=format&fit=crop&w=700&q=80',
        isVeg: true,
        isBestseller: false,
        rating: 4.8,
        calories: 220
      }
    ]
  }
];

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  private http = inject(HttpClient);

  constructor() {
    this.ensureCatalogSeeded();
  }

  getRestaurants(search?: string): Observable<Restaurant[]> {
    const url = search
      ? `${environment.apiUrl}/api/restaurants?search=${encodeURIComponent(search)}`
      : `${environment.apiUrl}/api/restaurants`;

    return this.http.get<Restaurant[]>(url).pipe(
      map(backendList => {
        if (!backendList || backendList.length === 0) {
          return this.filterApproved(search);
        }
        return backendList
          .map(b => this.enrichRestaurant(b))
          .filter(b => !b.status || b.status === 'APPROVED');
      }),
      catchError(() => {
        // Return only approved restaurants for customer marketplace
        return of(this.filterApproved(search));
      })
    );
  }

  getRestaurant(id: number): Observable<Restaurant> {
    return this.http.get<Restaurant>(`${environment.apiUrl}/api/restaurants/${id}`).pipe(
      map(res => this.enrichRestaurant(res)),
      catchError(() => {
        const catalog = this.getCatalog();
        const found = catalog.find(r => r.id === id) || catalog[0];
        return of(this.enrichRestaurant(found));
      })
    );
  }

  createRestaurant(req: RestaurantRequest): Observable<Restaurant> {
    return this.http.post<Restaurant>(`${environment.apiUrl}/api/restaurants`, req).pipe(
      catchError(() => {
        return this.submitRestaurant(req);
      })
    );
  }

  submitRestaurant(req: RestaurantRequest): Observable<Restaurant> {
    const catalog = this.getCatalog();
    const newRest: Restaurant = {
      id: Date.now(),
      name: req.name,
      description: req.description,
      address: req.address,
      cuisine: req.cuisine,
      rating: 4.8,
      open: true,
      imageUrl: req.imageUrl || 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=900&q=80',
      deliveryTime: req.deliveryTime || '25-30 mins',
      deliveryFee: 25,
      priceForTwo: req.priceForTwo || 450,
      distance: '2.1 km',
      offers: ['Welcome Offer 20% OFF'],
      featured: false,
      tags: ['New Partner', 'Handcrafted'],
      status: 'PENDING_APPROVAL',
      ownerId: req.ownerId,
      ownerName: req.ownerName || 'Restaurant Owner',
      ownerEmail: req.ownerEmail || 'vendor@quickbite.com',
      ownerPhone: req.ownerPhone || '+91 98765 00000',
      createdAt: new Date().toISOString(),
      menu: req.menu && req.menu.length > 0 ? req.menu : [
        {
          id: Date.now() + 1,
          name: 'Signature House Special',
          description: 'Chef recommendation crafted with seasonal ingredients.',
          price: 249,
          category: 'Mains',
          available: true,
          isVeg: true,
          isBestseller: true
        }
      ]
    };

    catalog.unshift(newRest);
    this.saveCatalog(catalog);
    return of(newRest);
  }

  approveRestaurant(id: number): Observable<Restaurant> {
    const catalog = this.getCatalog();
    const index = catalog.findIndex(r => r.id === id);
    if (index >= 0) {
      catalog[index] = {
        ...catalog[index],
        status: 'APPROVED',
        rejectionReason: undefined
      };
      this.saveCatalog(catalog);
      return of(catalog[index]);
    }
    return of(catalog[0]);
  }

  rejectRestaurant(id: number, reason?: string): Observable<Restaurant> {
    const catalog = this.getCatalog();
    const index = catalog.findIndex(r => r.id === id);
    if (index >= 0) {
      catalog[index] = {
        ...catalog[index],
        status: 'REJECTED',
        rejectionReason: reason || 'Information incomplete or failed quality standards'
      };
      this.saveCatalog(catalog);
      return of(catalog[index]);
    }
    return of(catalog[0]);
  }

  deleteRestaurant(id: number): Observable<boolean> {
    const catalog = this.getCatalog();
    const filtered = catalog.filter(r => r.id !== id);
    this.saveCatalog(filtered);
    return of(true);
  }

  getAllRestaurantsAdmin(): Observable<Restaurant[]> {
    return of(this.getCatalog());
  }

  getPendingRestaurants(): Observable<Restaurant[]> {
    const pending = this.getCatalog().filter(r => r.status === 'PENDING_APPROVAL');
    return of(pending);
  }

  getVendorRestaurants(ownerId: number): Observable<Restaurant[]> {
    const vendorOutlets = this.getCatalog().filter(r => r.ownerId === ownerId);
    return of(vendorOutlets);
  }

  addMenuItem(restaurantId: number, req: MenuItemRequest): Observable<MenuItem> {
    return this.http.post<MenuItem>(`${environment.apiUrl}/api/restaurants/${restaurantId}/menu`, req).pipe(
      catchError(() => {
        const catalog = this.getCatalog();
        const rest = catalog.find(r => r.id === restaurantId);
        const newItem: MenuItem = {
          id: Date.now(),
          name: req.name,
          description: req.description,
          price: req.price,
          category: req.category,
          available: req.available,
          imageUrl: req.imageUrl,
          isVeg: req.isVeg
        };
        if (rest) {
          rest.menu = rest.menu || [];
          rest.menu.push(newItem);
          this.saveCatalog(catalog);
        }
        return of(newItem);
      })
    );
  }

  private filterApproved(search?: string): Restaurant[] {
    const approved = this.getCatalog().filter(r => !r.status || r.status === 'APPROVED');
    if (!search || !search.trim()) return approved;
    const q = search.toLowerCase();
    return approved.filter(r =>
      r.name.toLowerCase().includes(q) ||
      r.cuisine.toLowerCase().includes(q) ||
      r.description.toLowerCase().includes(q) ||
      (r.menu && r.menu.some(m => m.name.toLowerCase().includes(q) || m.category.toLowerCase().includes(q)))
    );
  }

  private getCatalog(): Restaurant[] {
    try {
      const raw = localStorage.getItem('qb_restaurants_catalog');
      if (!raw) {
        const seeded = this.getInitialCatalog();
        localStorage.setItem('qb_restaurants_catalog', JSON.stringify(seeded));
        return seeded;
      }
      return JSON.parse(raw);
    } catch {
      return this.getInitialCatalog();
    }
  }

  private saveCatalog(list: Restaurant[]): void {
    try {
      localStorage.setItem('qb_restaurants_catalog', JSON.stringify(list));
    } catch {
      // ignore
    }
  }

  private ensureCatalogSeeded(): void {
    const raw = localStorage.getItem('qb_restaurants_catalog');
    if (!raw) {
      this.saveCatalog(this.getInitialCatalog());
    }
  }

  private getInitialCatalog(): Restaurant[] {
    const seeded: Restaurant[] = MOCK_RESTAURANTS.map(r => ({
      ...r,
      status: 'APPROVED',
      ownerId: 2,
      ownerName: 'Chef Marco Rossi (Vendor)',
      ownerEmail: 'vendor@quickbite.com',
      ownerPhone: '+91 98111 22334',
      createdAt: '2026-03-01T12:00:00.000Z'
    }));

    // Add 1 sample pending approval restaurant for admin demo
    const pendingSeed: Restaurant = {
      id: 701,
      name: 'The French Baker & Bistro',
      description: 'Freshly baked artisanal croissants, sourdough tartines, quiches, and French roast coffees.',
      address: '44, 100ft Road, Defence Colony, Indiranagar, Bengaluru',
      cuisine: 'Bakery · Cafe · French · Coffee',
      rating: 4.8,
      open: true,
      imageUrl: 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=900&q=80',
      deliveryTime: '25-30 mins',
      deliveryFee: 20,
      priceForTwo: 450,
      distance: '2.4 km',
      offers: ['20% OFF on first order'],
      featured: false,
      tags: ['New Cafe', 'Artisan Bakery', 'Trending'],
      status: 'PENDING_APPROVAL',
      ownerId: 5,
      ownerName: 'Rohit Gupta (Brew & Bake)',
      ownerEmail: 'rohit.cafe@example.com',
      ownerPhone: '+91 97777 66666',
      createdAt: new Date(Date.now() - 3600000 * 4).toISOString(),
      menu: [
        {
          id: 7001,
          name: 'Butter Croissant & Espresso Combo',
          description: 'Golden flaky French laminated croissant served with double espresso.',
          price: 199,
          category: 'Combos',
          available: true,
          imageUrl: 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=700&q=80',
          isVeg: true,
          isBestseller: true
        },
        {
          id: 7002,
          name: 'Avocado & Poached Egg Brioche Tartine',
          description: 'Hass avocado mash, seasoned heirloom tomatoes, microgreens on thick toasted brioche.',
          price: 289,
          category: 'Mains',
          available: true,
          imageUrl: 'https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=700&q=80',
          isVeg: false,
          isBestseller: true
        }
      ]
    };

    seeded.push(pendingSeed);
    return seeded;
  }

  private enrichRestaurant(r: Restaurant): Restaurant {
    const mock = MOCK_RESTAURANTS.find(m => m.id === r.id);
    return {
      ...r,
      imageUrl: r.imageUrl || mock?.imageUrl || 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=900&q=80',
      deliveryTime: r.deliveryTime || mock?.deliveryTime || '25-30 mins',
      deliveryFee: r.deliveryFee !== undefined ? r.deliveryFee : (mock?.deliveryFee ?? 25),
      priceForTwo: r.priceForTwo || mock?.priceForTwo || 500,
      distance: r.distance || mock?.distance || '2.0 km',
      offers: r.offers || mock?.offers || ['Up to 50% OFF', 'Free Delivery above ₹299'],
      featured: r.featured !== undefined ? r.featured : (mock?.featured ?? true),
      tags: r.tags || mock?.tags || ['Top Rated', 'Fast Delivery'],
      status: r.status || 'APPROVED',
      menu: r.menu && r.menu.length > 0 ? r.menu : (mock?.menu || [])
    };
  }
}
