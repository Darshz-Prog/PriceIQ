import axios from "axios";

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";
export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("priceiq_access_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export function saveSession(auth) {
  if (auth?.accessToken)
    localStorage.setItem("priceiq_access_token", auth.accessToken);
  if (auth?.refreshToken)
    localStorage.setItem("priceiq_refresh_token", auth.refreshToken);
  if (auth)
    localStorage.setItem(
      "priceiq_user",
      JSON.stringify({
        userId: auth.userId,
        firstName: auth.firstName,
        lastName: auth.lastName,
        email: auth.email,
        role: auth.role,
      }),
    );
}
export function clearSession() {
  ["priceiq_access_token", "priceiq_refresh_token", "priceiq_user"].forEach(
    (key) => localStorage.removeItem(key),
  );
}
export function getSession() {
  try {
    return JSON.parse(localStorage.getItem("priceiq_user") || "null");
  } catch {
    return null;
  }
}

// One place for every documented endpoint. Keep HTTP paths and parameter names aligned with openapi.json.
export const apiRoutes = {
  login: (body) => api.post("/api/v1/auth/login", body),
  register: (body) => api.post("/api/v1/auth/register", body),
  refreshToken: (token) =>
    api.post("/api/v1/auth/refresh-token", null, {
      headers: {
        Authorization: token.startsWith("Bearer ") ? token : `Bearer ${token}`,
      },
    }),
  products: (params = {}) => api.get("/api/v1/products", { params }),
  product: (id) => api.get(`/api/v1/products/${id}`),
  searchProducts: (params) => api.get("/api/v1/products/search", { params }),
  createProduct: (body) => api.post("/api/v1/products", body),
  updateProduct: (id, body) => api.put(`/api/v1/products/${id}`, body),
  deleteProduct: (id) => api.delete(`/api/v1/products/${id}`),
  feed: () => api.get("/api/v1/recommendations/feed"),
  cart: (userId) => api.get(`/api/v1/cart/${userId}`),
  addToCart: (body) => api.post("/api/v1/cart/add", body),
  updateCartQuantity: (id, quantity) =>
    api.put(`/api/v1/cart/update-quantity/${id}`, null, {
      params: { quantity },
    }),
  removeCartItem: (id) => api.delete(`/api/v1/cart/remove/${id}`),
  clearCart: (userId) => api.delete(`/api/v1/cart/clear/${userId}`),
  wishlist: (userId) => api.get(`/api/v1/wishlist/${userId}`),
  addWishlist: (body) => api.post("/api/v1/wishlist/add", body),
  removeWishlist: (userId, productId) =>
    api.delete(`/api/v1/wishlist/remove/${productId}`, { params: { userId } }),
  reviews: (productId) => api.get(`/api/v1/reviews/product/${productId}`),
  createReview: (body) => api.post("/api/v1/reviews", body),
  createOrder: (body) => api.post("/api/v1/orders", body),
  order: (id) => api.get(`/api/v1/orders/${id}`),
  orderHistory: (userId) => api.get(`/api/v1/orders/user/${userId}`),
  processPayment: (body) => api.post("/api/v1/payments", body),
  payment: (id) => api.get(`/api/v1/payments/${id}`),
  trackEvent: (body) => api.post("/api/v1/events", body),
  uploadImage: (file) => {
    const body = new FormData();
    body.append("file", file);
    return api.post("/api/images/upload", body, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
  analytics: () => api.get("/api/v1/analytics/dashboard"),
  trending: () => api.get("/api/v1/analytics/trending-products"),
  conversionRate: () => api.get("/api/v1/analytics/conversion-rate"),
};
