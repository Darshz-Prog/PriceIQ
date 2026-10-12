import React from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Layout from "./components/Layout";
import { ProductsPage, ProductDetailPage } from "./pages/StorePages";
import {
  CartPage,
  WishlistPage,
  AccountPage,
  CheckoutPage,
  OrderDetailPage,
  PaymentDetailPage,
} from "./pages/AccountPages";
import { LoginPage, RegisterPage } from "./pages/AuthPages";
import { AdminPage } from "./pages/AdminPage";
import "./styles.css";
function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<ProductsPage />} />
            <Route path="/search" element={<ProductsPage searchMode />} />
            <Route
              path="/recommendations"
              element={<ProductsPage feedMode />}
            />
            <Route path="/products/:id" element={<ProductDetailPage />} />
            <Route path="/cart" element={<CartPage />} />
            <Route path="/wishlist" element={<WishlistPage />} />
            <Route path="/account" element={<AccountPage />} />
            <Route path="/checkout" element={<CheckoutPage />} />
            <Route path="/orders/:id" element={<OrderDetailPage />} />
            <Route path="/payments/:id" element={<PaymentDetailPage />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="*"
              element={
                <div className="py-20 text-center">
                  <h1 className="text-3xl font-bold">Page not found</h1>
                  <a href="/" className="mt-4 inline-block text-brand">
                    Back to catalog
                  </a>
                </div>
              }
            />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
