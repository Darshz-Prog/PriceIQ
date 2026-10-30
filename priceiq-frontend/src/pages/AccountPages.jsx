import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  ArrowRight,
  ArrowUpRight,
  CheckCircle2,
  CreditCard,
  Heart,
  Minus,
  Package,
  Plus,
  ShoppingBag,
  Trash2,
  Truck,
  UserRound,
} from "lucide-react";
import { apiRoutes } from "../api/client";
import { useAuth } from "../context/AuthContext";
import {
  Badge,
  Button,
  Empty,
  Field,
  Loading,
  Notice,
  PageTitle,
} from "../components/UI";

const money = (v) =>
  typeof v === "number"
    ? new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
      }).format(v)
    : "—";

function getStatusBadge(status) {
  const map = {
    PENDING: "amber",
    CONFIRMED: "brand",
    SHIPPED: "brand",
    DELIVERED: "emerald",
    CANCELLED: "rose",
    COMPLETED: "emerald",
    SUCCESS: "emerald",
    FAILED: "rose",
  };
  return map[status] || "default";
}

export function CartPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  async function load() {
    if (!user) {
      setLoading(false);
      return;
    }
    try {
      setCart((await apiRoutes.cart(user.userId)).data);
    } catch (e) {
      setError(e.response?.data?.message || e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [user?.userId]);

  async function action(fn) {
    setError("");
    setNotice("");
    try {
      await fn();
      await load();
    } catch (e) {
      setError(e.response?.data?.message || e.message || "Request failed");
    }
  }

  if (loading) return <Loading />;
  if (!user)
    return (
      <Empty
        title="Your bag is waiting"
        detail="Sign in to view items in your shopping bag and proceed to checkout."
        action={
          <Link to="/login">
            <Button>Sign in to PriceIQ</Button>
          </Link>
        }
      />
    );

  const items = cart?.items || [];
  const totalItemCount = items.reduce((s, i) => s + (i.quantity || 0), 0);

  return (
    <>
      <PageTitle
        eyebrow="Your Bag"
        title="Shopping Cart"
        description={`${totalItemCount} ${totalItemCount === 1 ? "item" : "items"} in your cart ready for purchase.`}
        action={
          items.length > 0 && (
            <Button
              variant="secondary"
              onClick={() => action(() => apiRoutes.clearCart(user.userId))}
            >
              <Trash2 size={15} /> Clear Cart
            </Button>
          )
        }
      />

      {error && <Notice error={error} />}
      {notice && (
        <div className="mb-4">
          <Notice success={notice} onDismiss={() => setNotice("")} />
        </div>
      )}

      {!cart || items.length === 0 ? (
        <Empty
          title="Your cart is empty"
          detail="Explore our catalog and find items curated for you."
          action={
            <Link to="/">
              <Button>Explore Products</Button>
            </Link>
          }
        />
      ) : (
        <div className="grid gap-8 lg:grid-cols-[1fr_360px]">
          {/* Cart Items List */}
          <div className="space-y-3">
            {items.map((item) => (
              <article
                key={item.cartItemId}
                className="flex flex-col gap-4 rounded-2xl border border-slate-200/80 bg-white p-4 shadow-card transition sm:flex-row sm:items-center justify-between"
              >
                <div className="flex items-center gap-4">
                  <div className="grid h-20 w-20 shrink-0 place-items-center overflow-hidden rounded-xl bg-slate-50 border border-slate-100">
                    {item.productImage ? (
                      <img
                        src={item.productImage}
                        alt={item.productName}
                        className="h-full w-full object-cover"
                      />
                    ) : (
                      <ShoppingBag className="text-slate-300" size={24} />
                    )}
                  </div>
                  <div className="min-w-0">
                    <Link
                      to={`/products/${item.productId}`}
                      className="font-semibold text-slate-800 hover:text-brand line-clamp-1 text-sm"
                    >
                      {item.productName || `Product #${item.productId}`}
                    </Link>
                    <p className="mt-0.5 text-xs text-slate-500">
                      {money(item.priceAtAddition)} each
                    </p>

                    {/* Quantity Stepper */}
                    <div className="mt-2.5 flex items-center gap-2">
                      <button
                        aria-label="Decrease quantity"
                        disabled={item.quantity <= 1}
                        onClick={() =>
                          action(() =>
                            apiRoutes.updateCartQuantity(
                              item.cartItemId,
                              item.quantity - 1,
                            ),
                          )
                        }
                        className="grid h-7 w-7 place-items-center rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-30 active:scale-95"
                      >
                        <Minus size={13} />
                      </button>
                      <span className="min-w-6 text-center text-xs font-bold text-slate-800">
                        {item.quantity}
                      </span>
                      <button
                        aria-label="Increase quantity"
                        onClick={() =>
                          action(() =>
                            apiRoutes.updateCartQuantity(
                              item.cartItemId,
                              item.quantity + 1,
                            ),
                          )
                        }
                        className="grid h-7 w-7 place-items-center rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 active:scale-95"
                      >
                        <Plus size={13} />
                      </button>
                    </div>
                  </div>
                </div>

                <div className="flex items-center justify-between sm:flex-col sm:items-end gap-2 border-t border-slate-100 pt-3 sm:border-t-0 sm:pt-0">
                  <span className="text-base font-bold text-ink">
                    {money(item.totalPrice)}
                  </span>
                  <button
                    onClick={() =>
                      action(() => apiRoutes.removeCartItem(item.cartItemId))
                    }
                    className="flex items-center gap-1 text-xs text-slate-400 hover:text-rose-600 transition"
                  >
                    <Trash2 size={13} /> Remove
                  </button>
                </div>
              </article>
            ))}
          </div>

          {/* Summary Sidebar */}
          <aside className="h-fit rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
            <h2 className="text-base font-bold text-ink">Order Summary</h2>
            <div className="mt-5 space-y-3 text-sm text-slate-600">
              <div className="flex justify-between">
                <span>Subtotal ({totalItemCount} items)</span>
                <span className="font-semibold text-slate-800">
                  {money(cart.totalAmount)}
                </span>
              </div>
              <div className="flex justify-between text-xs text-emerald-600 font-medium">
                <span>Shipping</span>
                <span>FREE</span>
              </div>
            </div>

            <div className="mt-5 flex justify-between border-t border-slate-100 pt-4">
              <span className="font-bold text-slate-900">Total</span>
              <span className="text-2xl font-black text-ink">
                {money(cart.totalAmount)}
              </span>
            </div>

            <Button
              className="mt-6 w-full shadow-glow"
              size="lg"
              onClick={() => navigate("/checkout")}
            >
              Checkout <ArrowRight size={17} />
            </Button>
            <p className="mt-3 text-center text-xs text-slate-400">
              Safe & secure checkout powered by PriceIQ
            </p>
          </aside>
        </div>
      )}
    </>
  );
}

export function WishlistPage() {
  const { user } = useAuth();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  async function load() {
    if (!user) {
      setLoading(false);
      return;
    }
    try {
      setItems((await apiRoutes.wishlist(user.userId)).data || []);
    } catch (e) {
      setError(e.response?.data?.message || e.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [user?.userId]);

  async function remove(productId) {
    try {
      await apiRoutes.removeWishlist(user.userId, productId);
      setNotice("Item removed from wishlist.");
      await load();
    } catch (e) {
      setError(e.response?.data?.message || e.message);
    }
  }

  async function moveToCart(productId) {
    try {
      await apiRoutes.addToCart({
        userId: user.userId,
        productId,
        quantity: 1,
      });
      await apiRoutes.removeWishlist(user.userId, productId);
      setNotice("Moved to shopping cart!");
      await load();
    } catch (e) {
      setError("Could not move item to cart.");
    }
  }

  if (loading) return <Loading />;
  if (!user)
    return (
      <Empty
        title="Your wishlist is waiting"
        detail="Sign in to view your saved products across devices."
        action={
          <Link to="/login">
            <Button>Sign in to PriceIQ</Button>
          </Link>
        }
      />
    );

  return (
    <>
      <PageTitle
        eyebrow="Saved Items"
        title="Your Wishlist"
        description={`${items.length} ${items.length === 1 ? "product" : "products"} saved for later.`}
      />

      {error && <Notice error={error} />}
      {notice && (
        <div className="mb-4">
          <Notice success={notice} onDismiss={() => setNotice("")} />
        </div>
      )}

      {items.length === 0 ? (
        <Empty
          title="No saved items yet"
          detail="Click the heart icon on any product card to save it here."
          action={
            <Link to="/">
              <Button>Discover Products</Button>
            </Link>
          }
        />
      ) : (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {items.map((it) => (
            <article
              key={it.wishlistId || it.productId}
              className="group flex flex-col justify-between rounded-2xl border border-slate-200/80 bg-white p-4 shadow-card transition hover:border-slate-300 hover:shadow-hover"
            >
              <div className="flex gap-4">
                <div className="grid h-20 w-20 shrink-0 place-items-center overflow-hidden rounded-xl bg-slate-50 border border-slate-100">
                  {it.productImage ? (
                    <img
                      src={it.productImage}
                      alt={it.productName}
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <Heart className="text-slate-300" size={24} />
                  )}
                </div>
                <div className="min-w-0 flex-1">
                  <Link
                    to={`/products/${it.productId}`}
                    className="line-clamp-2 text-sm font-semibold text-slate-800 hover:text-brand"
                  >
                    {it.productName || `Product #${it.productId}`}
                  </Link>
                  <p className="mt-1 text-base font-bold text-ink">
                    {money(it.currentPrice)}
                  </p>
                </div>
              </div>

              <div className="mt-4 flex gap-2 border-t border-slate-100 pt-3">
                <Button
                  size="sm"
                  className="flex-1"
                  onClick={() => moveToCart(it.productId)}
                >
                  <ShoppingBag size={14} /> Move to Cart
                </Button>
                <button
                  onClick={() => remove(it.productId)}
                  className="grid h-8 w-8 place-items-center rounded-xl border border-slate-200 text-slate-400 hover:border-rose-200 hover:text-rose-600"
                  title="Remove"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            </article>
          ))}
        </div>
      )}
    </>
  );
}

export function AccountPage() {
  const { user, signOut } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }
    apiRoutes
      .orderHistory(user.userId)
      .then((r) => setOrders(Array.isArray(r.data) ? r.data : []))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, [user?.userId]);

  if (!user)
    return (
      <Empty
        title="Sign in to view your account"
        detail="Access your order history, profile details, and preferences."
        action={
          <Link to="/login">
            <Button>Sign In</Button>
          </Link>
        }
      />
    );

  return (
    <>
      <PageTitle
        eyebrow="My Account"
        title={`Hello, ${user.firstName || "Shopper"}`}
        description="Manage your profile information and track orders."
        action={
          <Button
            variant="secondary"
            onClick={() => {
              signOut();
              location.href = "/login";
            }}
          >
            Sign out
          </Button>
        }
      />

      <div className="grid gap-8 lg:grid-cols-[320px_1fr]">
        {/* Profile Card */}
        <section className="h-fit rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
          <div className="grid h-16 w-16 place-items-center rounded-2xl bg-brand-50 text-2xl font-black text-brand">
            {(user.firstName || "?")[0]}
            {(user.lastName || "")[0]}
          </div>
          <h2 className="mt-4 text-xl font-bold text-slate-800">
            {user.firstName} {user.lastName}
          </h2>
          <p className="mt-0.5 text-xs text-slate-500">{user.email}</p>

          <div className="mt-4">
            <Badge variant={user.role === "ADMIN" ? "brand" : "default"}>
              {user.role || "USER"}
            </Badge>
          </div>

          <div className="mt-6 divide-y divide-slate-100 text-xs">
            <div className="flex justify-between py-2 text-slate-500">
              <span>Account ID</span>
              <span className="font-mono font-semibold text-slate-800">
                #{user.userId}
              </span>
            </div>
            <div className="flex justify-between py-2 text-slate-500">
              <span>Status</span>
              <span className="font-semibold text-emerald-600">Active</span>
            </div>
          </div>

          <div className="mt-6 flex flex-col gap-2 border-t border-slate-100 pt-4">
            <Link
              to="/wishlist"
              className="flex items-center justify-between rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50"
            >
              <span className="flex items-center gap-2">
                <Heart size={15} className="text-brand" /> Wishlist
              </span>
              <ArrowRight size={14} className="text-slate-400" />
            </Link>
            <Link
              to="/cart"
              className="flex items-center justify-between rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50"
            >
              <span className="flex items-center gap-2">
                <ShoppingBag size={15} className="text-brand" /> Cart
              </span>
              <ArrowRight size={14} className="text-slate-400" />
            </Link>
          </div>
        </section>

        {/* Order History */}
        <section>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-xl font-bold text-ink">Order History</h2>
            <span className="text-xs text-slate-400">
              {orders.length} {orders.length === 1 ? "order" : "orders"}
            </span>
          </div>

          {error && <Notice error={error} />}
          {loading ? (
            <Loading />
          ) : orders.length === 0 ? (
            <Empty
              title="No orders placed yet"
              detail="When you purchase items, your order history will appear here."
              action={
                <Link to="/">
                  <Button>Start Shopping</Button>
                </Link>
              }
            />
          ) : (
            <div className="space-y-3">
              {orders.map((o, i) => (
                <Link
                  to={`/orders/${o.orderId}`}
                  key={o.orderId || i}
                  className="flex flex-col gap-4 rounded-2xl border border-slate-200/80 bg-white p-5 shadow-card transition hover:border-brand-200 hover:shadow-hover sm:flex-row sm:items-center sm:justify-between"
                >
                  <div className="flex items-center gap-3.5">
                    <div className="grid h-12 w-12 shrink-0 place-items-center rounded-xl bg-slate-50 text-slate-600 border border-slate-100">
                      <Package size={20} />
                    </div>
                    <div>
                      <p className="font-bold text-slate-800 text-sm">
                        {o.orderNumber || `Order #${o.orderId}`}
                      </p>
                      <p className="mt-0.5 text-xs text-slate-400">
                        {o.createdAt
                          ? new Date(o.createdAt).toLocaleDateString(undefined, {
                              year: "numeric",
                              month: "short",
                              day: "numeric",
                            })
                          : "Recent"}
                        {" · "}
                        {o.items?.length || 0} line items
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-4 sm:text-right">
                    <Badge variant={getStatusBadge(o.orderStatus)}>
                      {o.orderStatus || "PENDING"}
                    </Badge>
                    <span className="text-base font-extrabold text-ink">
                      {money(o.totalAmount)}
                    </span>
                    <ArrowRight size={16} className="text-slate-400" />
                  </div>
                </Link>
              ))}
            </div>
          )}
        </section>
      </div>
    </>
  );
}

export function CheckoutPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [form, setForm] = useState({
    shippingAddress: "",
    city: "",
    state: "",
    country: "India",
    postalCode: "",
    couponCode: "",
  });
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [order, setOrder] = useState(null);

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }
    apiRoutes
      .cart(user.userId)
      .then((r) => setCart(r.data))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, [user?.userId]);

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function submit(e) {
    e.preventDefault();
    if (!user) return;
    setBusy(true);
    setError("");
    try {
      const body = {
        userId: user.userId,
        items: (cart?.items || []).map((i) => ({
          productId: i.productId,
          quantity: i.quantity,
        })),
        ...form,
      };
      const { data } = await apiRoutes.createOrder(body);
      setOrder(data);

      // Track purchase event for Kafka event recommendation pipeline
      for (const item of (cart?.items || [])) {
        apiRoutes
          .trackEvent({
            userId: user.userId,
            productId: item.productId,
            eventType: "PURCHASE",
          })
          .catch(() => {});
      }

      await apiRoutes.clearCart(user.userId).catch(() => {});
    } catch (e) {
      setError(
        e.response?.data?.message || e.message || "Could not create order.",
      );
    } finally {
      setBusy(false);
    }
  }

  if (loading) return <Loading />;
  if (!user)
    return (
      <Empty
        title="Sign in before checkout"
        detail="An authenticated account is required to place an order."
        action={
          <Link to="/login">
            <Button>Sign In</Button>
          </Link>
        }
      />
    );

  if (order)
    return (
      <div className="mx-auto max-w-lg rounded-3xl border border-emerald-200 bg-white p-8 text-center shadow-card animate-fade-in">
        <div className="mx-auto grid h-16 w-16 place-items-center rounded-2xl bg-emerald-50 text-emerald-600">
          <CheckCircle2 size={32} />
        </div>
        <h1 className="mt-5 text-2xl font-black text-ink">Order Placed!</h1>
        <p className="mt-2 text-sm text-slate-500">
          Order number{" "}
          <span className="font-mono font-bold text-slate-800">
            {order.orderNumber || `#${order.orderId}`}
          </span>{" "}
          has been confirmed.
        </p>
        <p className="mt-4 text-3xl font-black text-brand">
          {money(order.totalAmount)}
        </p>
        <div className="mt-8 flex flex-col sm:flex-row justify-center gap-3">
          <Button onClick={() => navigate(`/orders/${order.orderId}`)}>
            View Order & Pay
          </Button>
          <Button variant="secondary" onClick={() => navigate("/")}>
            Continue Shopping
          </Button>
        </div>
      </div>
    );

  const items = cart?.items || [];

  return (
    <>
      <PageTitle
        eyebrow="Checkout"
        title="Shipping & Delivery"
        description="Confirm your delivery address and review order line items."
      />

      {error && <Notice error={error} />}

      {items.length === 0 ? (
        <Empty
          title="Your cart is empty"
          detail="Add products to your cart before proceeding to checkout."
          action={
            <Link to="/">
              <Button>Explore Products</Button>
            </Link>
          }
        />
      ) : (
        <div className="grid gap-8 lg:grid-cols-[1fr_360px]">
          {/* Shipping Form */}
          <form
            onSubmit={submit}
            className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card space-y-4"
          >
            <h2 className="text-base font-bold text-ink">Delivery Address</h2>

            <Field
              label="Street Address"
              name="shippingAddress"
              required
              value={form.shippingAddress}
              onChange={change}
              placeholder="e.g. 124 Park Avenue, Suite 4B"
            />

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field
                label="City"
                name="city"
                required
                value={form.city}
                onChange={change}
                placeholder="e.g. Mumbai"
              />
              <Field
                label="State"
                name="state"
                required
                value={form.state}
                onChange={change}
                placeholder="e.g. Maharashtra"
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field
                label="Postal Code"
                name="postalCode"
                required
                value={form.postalCode}
                onChange={change}
                placeholder="e.g. 400001"
              />
              <Field
                label="Country"
                name="country"
                required
                value={form.country}
                onChange={change}
              />
            </div>

            <Field
              label="Coupon Code (Optional)"
              name="couponCode"
              value={form.couponCode}
              onChange={change}
              placeholder="e.g. WELCOME10"
            />

            <div className="pt-4 border-t border-slate-100">
              <Button
                type="submit"
                disabled={busy}
                loading={busy}
                size="lg"
                className="w-full shadow-glow"
              >
                Place Order · {money(cart.totalAmount)}
              </Button>
            </div>
          </form>

          {/* Cart Preview */}
          <aside className="h-fit rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card space-y-4">
            <h2 className="text-base font-bold text-ink">Items in Order</h2>
            <div className="divide-y divide-slate-100 max-h-80 overflow-y-auto">
              {items.map((i) => (
                <div key={i.cartItemId} className="flex justify-between py-3 text-xs">
                  <div>
                    <p className="font-semibold text-slate-800 line-clamp-1">
                      {i.productName}
                    </p>
                    <p className="text-slate-400 mt-0.5">Qty: {i.quantity}</p>
                  </div>
                  <span className="font-bold text-slate-900">{money(i.totalPrice)}</span>
                </div>
              ))}
            </div>

            <div className="border-t border-slate-100 pt-4 flex justify-between font-bold text-ink">
              <span>Total Amount</span>
              <span className="text-xl text-brand">{money(cart.totalAmount)}</span>
            </div>
          </aside>
        </div>
      )}
    </>
  );
}

export function OrderDetailPage() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id) return;
    apiRoutes
      .order(id)
      .then((r) => setOrder(r.data))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Loading />;
  if (error) return <Notice error={error} />;
  if (!order) return <Empty title="Order not found" />;

  const items = order.items || [];

  return (
    <>
      <PageTitle
        eyebrow="Order Details"
        title={order.orderNumber || `Order #${order.orderId}`}
        description="Review items, delivery address, and proceed with payment."
        action={
          <Badge variant={getStatusBadge(order.orderStatus)}>
            {order.orderStatus || "PENDING"}
          </Badge>
        }
      />

      <div className="grid gap-8 lg:grid-cols-[1fr_380px]">
        {/* Order Items */}
        <div className="space-y-4">
          <div className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
            <h2 className="text-base font-bold text-ink mb-4">Ordered Items</h2>
            <div className="divide-y divide-slate-100">
              {items.map((it) => (
                <div key={it.orderItemId} className="flex items-center justify-between py-3">
                  <div>
                    <Link
                      to={`/products/${it.productId}`}
                      className="text-sm font-semibold text-slate-800 hover:text-brand"
                    >
                      {it.productName || `Product #${it.productId}`}
                    </Link>
                    <p className="text-xs text-slate-400 mt-0.5">
                      {money(it.pricePerUnit)} × {it.quantity}
                    </p>
                  </div>
                  <span className="text-sm font-bold text-slate-900">
                    {money(it.totalPrice)}
                  </span>
                </div>
              ))}
            </div>

            <div className="mt-4 flex justify-between border-t border-slate-100 pt-4 font-bold text-ink">
              <span>Order Total</span>
              <span className="text-xl text-brand">{money(order.totalAmount)}</span>
            </div>
          </div>

          {/* Shipping Info */}
          <div className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card text-xs">
            <h2 className="text-base font-bold text-ink mb-3">Shipping Address</h2>
            <p className="text-slate-700 font-medium">{order.shippingAddress}</p>
            <p className="text-slate-500 mt-1">
              {order.city}, {order.state} - {order.postalCode}
            </p>
            {order.trackingNumber && (
              <p className="mt-3 text-slate-700">
                Tracking Number:{" "}
                <span className="font-mono font-bold text-brand">{order.trackingNumber}</span>
              </p>
            )}
          </div>
        </div>

        {/* Payment Form Widget */}
        <PaymentForm order={order} />
      </div>
    </>
  );
}

function PaymentForm({ order }) {
  const navigate = useNavigate();
  const [paymentMethod, setPaymentMethod] = useState("UPI");
  const [transactionId, setTransactionId] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  return (
    <section className="h-fit rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
      <div className="flex items-center gap-2">
        <CreditCard size={18} className="text-brand" />
        <h2 className="text-base font-bold text-ink">Complete Payment</h2>
      </div>
      <p className="mt-1 text-xs text-slate-500 leading-relaxed">
        Select your payment method and enter your transaction ID reference.
      </p>

      {error && (
        <div className="mt-3">
          <Notice error={error} />
        </div>
      )}

      <form
        className="mt-4 space-y-4"
        onSubmit={async (e) => {
          e.preventDefault();
          setBusy(true);
          setError("");
          try {
            const { data } = await apiRoutes.processPayment({
              orderId: order.orderId,
              paymentMethod,
              transactionId,
            });
            if (data.paymentId) navigate(`/payments/${data.paymentId}`);
            else setError("Payment processed without ID.");
          } catch (e) {
            setError(
              e.response?.data?.message ||
                e.message ||
                "Payment request failed.",
            );
          } finally {
            setBusy(false);
          }
        }}
      >
        <label className="block text-xs font-bold uppercase tracking-wider text-slate-600">
          Payment Method
          <select
            className="mt-1.5 w-full rounded-xl border border-slate-200 bg-white p-3 text-sm outline-none focus:border-brand"
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
          >
            {["CARD", "UPI", "NET_BANKING", "WALLET", "CASH_ON_DELIVERY"].map(
              (v) => (
                <option key={v} value={v}>
                  {v}
                </option>
              ),
            )}
          </select>
        </label>

        <Field
          label="Transaction Reference / ID"
          required
          value={transactionId}
          onChange={(e) => setTransactionId(e.target.value)}
          placeholder="e.g. UPI-REF-987654"
        />

        <Button
          type="submit"
          size="lg"
          disabled={busy}
          loading={busy}
          className="w-full shadow-glow"
        >
          Pay {money(order.totalAmount)}
        </Button>
      </form>
    </section>
  );
}

export function PaymentDetailPage() {
  const { id } = useParams();
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id) return;
    apiRoutes
      .payment(id)
      .then((r) => setPayment(r.data))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Loading />;
  if (error) return <Notice error={error} />;
  if (!payment) return <Empty title="Payment receipt not found" />;

  return (
    <>
      <PageTitle
        eyebrow="Payment Receipt"
        title={`Payment #${payment.paymentId}`}
        description="Official payment status returned by the PriceIQ transaction engine."
      />

      <div className="mx-auto max-w-xl rounded-3xl border border-slate-200/80 bg-white p-8 shadow-card">
        <div className="flex items-center justify-between border-b border-slate-100 pb-5">
          <div>
            <span className="text-xs text-slate-400 uppercase font-bold">Status</span>
            <div className="mt-1">
              <Badge variant={getStatusBadge(payment.paymentStatus)}>
                {payment.paymentStatus}
              </Badge>
            </div>
          </div>
          <div className="text-right">
            <span className="text-xs text-slate-400 uppercase font-bold">Total Amount</span>
            <div className="text-2xl font-black text-ink">{money(payment.amount)}</div>
          </div>
        </div>

        <div className="mt-6 space-y-3.5 text-xs">
          {[
            ["Order ID", `#${payment.orderId}`],
            ["Transaction ID", payment.transactionId],
            ["Payment Method", payment.paymentMethod],
            ["Gateway", payment.paymentGateway || "Internal"],
            [
              "Date & Time",
              payment.createdAt
                ? new Date(payment.createdAt).toLocaleString()
                : "—",
            ],
          ].map(([k, v]) => (
            <div key={k} className="flex justify-between border-b border-slate-50 pb-2.5">
              <span className="text-slate-500 font-medium">{k}</span>
              <span className="font-semibold text-slate-800">{v ?? "—"}</span>
            </div>
          ))}
        </div>

        <div className="mt-8 flex justify-center gap-3">
          <Link to={`/orders/${payment.orderId}`}>
            <Button variant="secondary">View Order</Button>
          </Link>
          <Link to="/">
            <Button>Back to Catalog</Button>
          </Link>
        </div>
      </div>
    </>
  );
}
