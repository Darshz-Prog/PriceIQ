# PriceIQ frontend

React + Vite + Tailwind CSS frontend for the supplied PriceIQ OpenAPI contract.

## Run locally

```bash
npm install
cp .env.example .env
npm run dev
```

The default API origin is `http://localhost:8081`. Override it with `VITE_API_BASE_URL` if needed. Start the Spring Boot backend separately. If browser requests fail with a CORS error, configure CORS on the backend for the Vite development origin; this frontend does not modify backend security/CORS configuration.

## Pages and API mapping

- Catalog and search: `GET /api/v1/products`, `GET /api/v1/products/search`
- Product detail/reviews: `GET /api/v1/products/{productId}`, `GET /api/v1/reviews/product/{productId}`, `POST /api/v1/reviews`
- Auth: `POST /api/v1/auth/login`, `POST /api/v1/auth/register`; access token is attached as Bearer token, refresh token is stored for explicit refresh handling
- Cart: `GET /api/v1/cart/{userId}`, `POST /api/v1/cart/add`, `PUT /api/v1/cart/update-quantity/{cartItemId}?quantity=...`, `DELETE /api/v1/cart/remove/{cartItemId}`, `DELETE /api/v1/cart/clear/{userId}`
- Wishlist: `GET /api/v1/wishlist/{userId}`, `POST /api/v1/wishlist/add`, `DELETE /api/v1/wishlist/remove/{productId}?userId=...`
- Checkout/orders: `POST /api/v1/orders`, `GET /api/v1/orders/{orderId}`, `GET /api/v1/orders/user/{userId}`
- Admin view: analytics dashboard/trending/conversion endpoints and `POST /api/v1/products`

All endpoint functions, including endpoints not surfaced in the initial UI, are centralized in `src/api/client.js`.

## Contract caveats

- OpenAPI applies global `bearerAuth` security to every operation, including login/register, and defines no operation-level security overrides. The UI allows a first login/register request without a token so the authentication flow can start; confirm the intended public endpoints in the backend/OpenAPI contract.
- The contract gives `role` values `USER` and `ADMIN`, but does not document per-operation role restrictions. The analytics/product-create view is shown to a session whose returned role is `ADMIN`; this is a UI affordance, not a replacement for backend authorization.
- `sortBy` has no documented enum, and some responses (`GET reviews`, order history, analytics trending/conversion) have generic `object` schemas. The frontend uses only known fields and renders generic analytics payloads as returned.
- Checkout creates an order, but the contract does not specify payment initiation as part of order creation. Payment processing is not automatically triggered; use the documented payments endpoint only when the caller has the required `orderId`, `paymentMethod`, and `transactionId`.
- Recommendation feed is available at `/recommendations` and uses the documented endpoint; it is not silently substituted for the product catalog.

## Build

```bash
npm run build
```
