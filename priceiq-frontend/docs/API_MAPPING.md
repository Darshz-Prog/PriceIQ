# OpenAPI contract mapping

**Specification:** PriceIQ API v1.0.0 · server `http://localhost:8081`

## Security review

- Global security declares HTTP Bearer JWT for every operation. No operation-level `security: []` exceptions are defined, so OpenAPI formally marks login and registration as bearer-protected too. The UI sends the token when one exists but permits the initial login/register calls so the auth flow can begin; verify this contract/backend mismatch.
- The `AuthResponse.role` enum is `USER` or `ADMIN`, but no endpoint has a documented role restriction. UI-only role checks are not security controls.
- The API contract does not specify CORS behavior. The backend must permit the frontend origin for browser requests.

## Endpoint inventory

- **GET `/api/v1/products/{productId}`** — `getProductById`. Params: `productId` (path, required). Request: `none`. Response: `ProductResponse`.
- **PUT `/api/v1/products/{productId}`** — `updateProduct`. Params: `productId` (path, required). Request: `ProductRequest`. Response: `ProductResponse`.
- **DELETE `/api/v1/products/{productId}`** — `deleteProduct`. Params: `productId` (path, required). Request: `none`. Response: `No body specified`.
- **PUT `/api/v1/cart/update-quantity/{cartItemId}`** — `updateQuantity`. Params: `cartItemId` (path, required), `quantity` (query, required). Request: `none`. Response: `CartResponse`.
- **POST `/api/v1/wishlist/add`** — `addToWishlist`. Params: —. Request: `WishlistRequest`. Response: `WishlistResponse`.
- **POST `/api/v1/reviews`** — `createReview`. Params: —. Request: `ReviewRequest`. Response: `ReviewResponse`.
- **GET `/api/v1/products`** — `getAllProducts`. Params: `page` (query), `size` (query), `category` (query), `brand` (query), `minPrice` (query), `maxPrice` (query), `sortBy` (query). Request: `none`. Response: `PageProductResponse`.
- **POST `/api/v1/products`** — `createProduct`. Params: —. Request: `ProductRequest`. Response: `ProductResponse`.
- **POST `/api/v1/payments`** — `processPayment`. Params: —. Request: `PaymentRequest`. Response: `PaymentResponse`.
- **POST `/api/v1/orders`** — `createOrder`. Params: —. Request: `OrderRequest`. Response: `OrderResponse`.
- **POST `/api/v1/events`** — `trackEvent`. Params: —. Request: `EventRequest`. Response: `GenericSuccessResponse`.
- **POST `/api/v1/cart/add`** — `addToCart`. Params: —. Request: `CartRequest`. Response: `CartResponse`.
- **POST `/api/v1/auth/register`** — `register`. Params: —. Request: `RegisterRequest`. Response: `AuthResponse`.
- **POST `/api/v1/auth/refresh-token`** — `refreshToken`. Params: `Authorization` (header, required). Request: `none`. Response: `AuthResponse`.
- **POST `/api/v1/auth/login`** — `login`. Params: —. Request: `LoginRequest`. Response: `AuthResponse`.
- **POST `/api/images/upload`** — `uploadImage`. Params: —. Request: `object (file)`. Response: `ImageUploadResponse`.
- **GET `/api/v1/wishlist/{userId}`** — `getWishlist`. Params: `userId` (path, required). Request: `none`. Response: `array of WishlistResponse`.
- **GET `/api/v1/reviews/product/{productId}`** — `getReviewsByProduct`. Params: `productId` (path, required). Request: `none`. Response: `object`.
- **GET `/api/v1/recommendations/feed`** — `getFeed`. Params: —. Request: `none`. Response: `array of ProductResponse`.
- **GET `/api/v1/products/search`** — `searchProducts`. Params: `keyword` (query, required), `page` (query), `size` (query). Request: `none`. Response: `PageProductResponse`.
- **GET `/api/v1/payments/{paymentId}`** — `getPaymentDetails`. Params: `paymentId` (path, required). Request: `none`. Response: `PaymentResponse`.
- **GET `/api/v1/orders/{orderId}`** — `getOrderById`. Params: `orderId` (path, required). Request: `none`. Response: `OrderResponse`.
- **GET `/api/v1/orders/user/{userId}`** — `getOrderHistory`. Params: `userId` (path, required). Request: `none`. Response: `object`.
- **GET `/api/v1/cart/{userId}`** — `getCart`. Params: `userId` (path, required). Request: `none`. Response: `CartResponse`.
- **GET `/api/v1/analytics/trending-products`** — `getTrendingProducts`. Params: —. Request: `none`. Response: `object`.
- **GET `/api/v1/analytics/dashboard`** — `getDashboardAnalytics`. Params: —. Request: `none`. Response: `AnalyticsResponse`.
- **GET `/api/v1/analytics/conversion-rate`** — `getConversionRate`. Params: —. Request: `none`. Response: `object`.
- **DELETE `/api/v1/wishlist/remove/{productId}`** — `removeFromWishlist`. Params: `userId` (query, required), `productId` (path, required). Request: `none`. Response: `WishlistResponse`.
- **DELETE `/api/v1/cart/remove/{cartItemId}`** — `removeFromCart`. Params: `cartItemId` (path, required). Request: `none`. Response: `CartResponse`.
- **DELETE `/api/v1/cart/clear/{userId}`** — `clearCart`. Params: `userId` (path, required). Request: `none`. Response: `No body specified`.

## Schema field reference

### `ProductImageRequest`
`imageUrl`: string, `primaryImage`: boolean, `displayOrder`: integer (int32), `altText`: string

### `ProductRequest`
`name`: string, `description`: string, `sku`: string, `brand`: string, `category`: string, `basePrice`: number, `currentPrice`: number, `stockQuantity`: integer (int32), `active`: boolean, `images`: array, `attributes`: object

### `ProductImageResponse`
`id`: integer (int64), `imageUrl`: string, `primaryImage`: boolean, `displayOrder`: integer (int32), `altText`: string

### `ProductResponse`
`id`: integer (int64), `name`: string, `description`: string, `sku`: string, `brand`: string, `category`: string, `basePrice`: number, `currentPrice`: number, `stockQuantity`: integer (int32), `averageRating`: number (double), `totalReviews`: integer (int32), `active`: boolean, `images`: array, `attributes`: object

### `CartItemResponse`
`cartItemId`: integer (int64), `productId`: integer (int64), `productName`: string, `productImage`: string, `quantity`: integer (int32), `priceAtAddition`: number, `totalPrice`: number

### `CartResponse`
`cartId`: integer (int64), `userId`: integer (int64), `items`: array, `totalAmount`: number

### `WishlistRequest`
`userId`*: integer (int64), `productId`*: integer (int64)

### `WishlistResponse`
`wishlistId`: integer (int64), `userId`: integer (int64), `productId`: integer (int64), `productName`: string, `productImage`: string, `brand`: string, `category`: string

### `ReviewRequest`
`userId`*: integer (int64), `productId`*: integer (int64), `rating`*: integer (int32), `comment`: string

### `ReviewResponse`
`reviewId`: integer (int64), `userId`: integer (int64), `productId`: integer (int64), `userName`: string, `rating`: integer (int32), `comment`: string, `verifiedPurchase`: boolean, `createdAt`: string (date-time)

### `PaymentRequest`
`orderId`*: integer (int64), `paymentMethod`*: string enum ['CARD', 'UPI', 'NET_BANKING', 'WALLET', 'CASH_ON_DELIVERY'], `transactionId`*: string

### `PaymentResponse`
`paymentId`: integer (int64), `orderId`: integer (int64), `transactionId`: string, `paymentMethod`: string enum ['CARD', 'UPI', 'NET_BANKING', 'WALLET', 'CASH_ON_DELIVERY'], `paymentStatus`: string enum ['PENDING', 'SUCCESS', 'FAILED', 'REFUNDED'], `amount`: number, `paymentGateway`: string, `createdAt`: string (date-time)

### `OrderItemRequest`
`productId`*: integer (int64), `quantity`*: integer (int32)

### `OrderRequest`
`userId`*: integer (int64), `items`*: array, `shippingAddress`*: string, `city`*: string, `state`*: string, `country`*: string, `postalCode`*: string, `couponCode`: string

### `OrderItemResponse`
`orderItemId`: integer (int64), `productId`: integer (int64), `productName`: string, `quantity`: integer (int32), `pricePerUnit`: number, `totalPrice`: number

### `OrderResponse`
`orderId`: integer (int64), `userId`: integer (int64), `orderNumber`: string, `orderStatus`: string enum ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURNED'], `subtotal`: number, `shippingCharge`: number, `taxAmount`: number, `discountAmount`: number, `totalAmount`: number, `shippingAddress`: string, `city`: string, `state`: string, `country`: string, `postalCode`: string, `trackingNumber`: string, `items`: array, `createdAt`: string (date-time)

### `EventRequest`
`userId`: integer (int64), `eventType`: string enum ['PRODUCT_VIEW', 'PRODUCT_CLICK', 'SEARCH', 'ADD_TO_CART', 'REMOVE_FROM_CART', 'ADD_TO_WISHLIST', 'REMOVE_FROM_WISHLIST', 'PURCHASE', 'REVIEW', 'LOGIN'], `productId`: integer (int64), `searchKeyword`: string, `sessionId`: string, `metadata`: string, `timestamp`: string (date-time)

### `GenericSuccessResponse`
`success`: boolean, `message`: string

### `CartRequest`
`userId`*: integer (int64), `productId`*: integer (int64), `quantity`*: integer (int32)

### `RegisterRequest`
`firstName`*: string, `lastName`*: string, `email`*: string, `password`*: string, `phoneNumber`: string

### `AuthResponse`
`userId`: integer (int64), `firstName`: string, `lastName`: string, `email`: string, `role`: string enum ['USER', 'ADMIN'], `accessToken`: string, `refreshToken`: string

### `LoginRequest`
`email`*: string, `password`*: string

### `ImageUploadResponse`
`imageUrl`: string

### `PageProductResponse`
`totalElements`: integer (int64), `totalPages`: integer (int32), `pageable`: #/components/schemas/PageableObject, `size`: integer (int32), `content`: array, `number`: integer (int32), `sort`: #/components/schemas/SortObject, `numberOfElements`: integer (int32), `first`: boolean, `last`: boolean, `empty`: boolean

### `PageableObject`
`paged`: boolean, `pageNumber`: integer (int32), `pageSize`: integer (int32), `unpaged`: boolean, `offset`: integer (int64), `sort`: #/components/schemas/SortObject

### `SortObject`
`sorted`: boolean, `unsorted`: boolean, `empty`: boolean

### `AnalyticsResponse`
`totalUsers`: integer (int64), `totalOrders`: integer (int64), `totalProducts`: integer (int64), `totalEvents`: integer (int64), `totalRevenue`: number, `totalReviews`: integer (int64), `trendingProducts`: array, `ordersByStatus`: object, `eventsByType`: object, `conversionRates`: object


`*` marks required fields where the schema declares required properties. Other request properties are optional in OpenAPI unless stated otherwise.
