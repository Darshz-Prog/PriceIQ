import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  ArrowUpRight,
  Heart,
  Minus,
  Plus,
  ShoppingBag,
  Trash2,
} from "lucide-react";
import { apiRoutes } from "../api/client";
import { useAuth } from "../context/AuthContext";
import {
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
        maximumFractionDigits: 2,
      }).format(v)
    : "—";

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
        title="Your cart starts here"
        detail="Sign in to view and manage your backend cart."
      />
    );
  const items = cart?.items || [];
  return (
    <>
      <PageTitle
        eyebrow="Your bag"
        title="Shopping cart"
        description="Cart quantities and totals are supplied by the PriceIQ API."
        action={
          items.length > 0 && (
            <Button
              variant="secondary"
              onClick={() => action(() => apiRoutes.clearCart(user.userId))}
            >
              <Trash2 size={16} /> Clear cart
            </Button>
          )
        }
      />
      {error && <Notice error={error} />}
      {notice && <p className="mb-4 text-sm text-emerald-700">{notice}</p>}
      {!cart || items.length === 0 ? (
        <Empty
          title="Your cart is empty"
          detail="Browse the catalog and add something you like."
        />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[1fr_330px]">
          <div className="space-y-3">
            {items.map((item) => (
              <article
                key={item.cartItemId}
                className="flex flex-col gap-4 rounded-2xl border border-slate-200 bg-white p-4 sm:flex-row sm:items-center"
              >
                <div className="grid h-24 w-24 shrink-0 place-items-center overflow-hidden rounded-xl bg-slate-100">
                  {item.productImage ? (
                    <img
                      src={item.productImage}
                      alt={item.productName}
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <ShoppingBag className="text-slate-300" />
                  )}
                </div>
                <div className="min-w-0 flex-1">
                  <Link
                    to={`/products/${item.productId}`}
                    className="font-semibold text-slate-800 hover:text-brand"
                  >
                    {item.productName || `Product #${item.productId}`}
                  </Link>
                  <p className="mt-1 text-sm text-slate-500">
                    {money(item.priceAtAddition)} each
                  </p>
                  <div className="mt-3 flex items-center gap-2">
                    <Button
                      variant="secondary"
                      className="!p-2"
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
                    >
                      <Minus size={15} />
                    </Button>
                    <span className="min-w-6 text-center text-sm font-semibold">
                      {item.quantity}
                    </span>
                    <Button
                      variant="secondary"
                      className="!p-2"
                      aria-label="Increase quantity"
                      onClick={() =>
                        action(() =>
                          apiRoutes.updateCartQuantity(
                            item.cartItemId,
                            item.quantity + 1,
                          ),
                        )
                      }
                    >
                      <Plus size={15} />
                    </Button>
                  </div>
                </div>
                <div className="flex items-center justify-between gap-3 sm:flex-col sm:items-end">
                  <b className="text-lg">{money(item.totalPrice)}</b>
                  <Button
                    variant="ghost"
                    className="!p-2 text-rose-600"
                    onClick={() =>
                      action(() => apiRoutes.removeCartItem(item.cartItemId))
                    }
                  >
                    <Trash2 size={16} /> Remove
                  </Button>
                </div>
              </article>
            ))}
          </div>
          <aside className="h-fit rounded-2xl border border-slate-200 bg-white p-5">
            <h2 className="text-lg font-bold">Order summary</h2>
            <div className="mt-5 flex justify-between text-sm text-slate-500">
              <span>Items</span>
              <span>{items.reduce((s, i) => s + (i.quantity || 0), 0)}</span>
            </div>
            <div className="mt-4 flex justify-between border-t border-slate-100 pt-4">
              <span className="font-semibold">Cart total</span>
              <b className="text-xl">{money(cart.totalAmount)}</b>
            </div>
            <Button
              block
              className="mt-5"
              onClick={() => navigate("/checkout")}
            >
              Continue to checkout <ArrowUpRight size={17} />
            </Button>
            <p className="mt-3 text-xs leading-5 text-slate-400">
              Order creation and payment are separate backend operations.
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
  async function remove(id) {
    try {
      await apiRoutes.removeWishlist(user.userId, id);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || e.message);
    }
  }
  if (loading) return <Loading />;
  if (!user)
    return (
      <Empty
        title="Your wishlist is waiting"
        detail="Sign in to see saved products."
      />
    );
  return (
    <>
      <PageTitle
        eyebrow="Saved items"
        title="Your wishlist"
        description="Products saved to your account."
      />
      {error && <Notice error={error} />}
      {items.length === 0 ? (
        <Empty
          title="No saved products yet"
          detail="Tap the heart on a product to save it here."
        />
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {items.map((p, i) => (
            <article
              key={p.wishlistId || `${p.productId}-${i}`}
              className="flex items-center gap-4 rounded-2xl border border-slate-200 bg-white p-4"
            >
              {p.productImage ? (
                <img
                  src={p.productImage}
                  alt={p.productName}
                  className="h-20 w-20 rounded-xl bg-slate-100 object-cover"
                />
              ) : (
                <div className="grid h-20 w-20 place-items-center rounded-xl bg-slate-100">
                  <Heart className="text-slate-300" />
                </div>
              )}
              <div className="min-w-0 flex-1">
                <Link
                  to={`/products/${p.productId}`}
                  className="font-semibold hover:text-brand"
                >
                  {p.productName || `Product #${p.productId}`}
                </Link>
                <p className="mt-1 text-xs text-slate-500">
                  {p.brand || p.category || "Saved product"}
                </p>
                <div className="mt-2 flex gap-2">
                  <Link
                    className="text-xs font-semibold text-brand"
                    to={`/products/${p.productId}`}
                  >
                    View product
                  </Link>
                  <button
                    className="text-xs font-semibold text-rose-600"
                    onClick={() => remove(p.productId)}
                  >
                    Remove
                  </button>
                </div>
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
        detail="Your profile and order history are returned by the authentication and orders APIs."
      />
    );
  return (
    <>
      <PageTitle
        eyebrow="Account"
        title={`Hello, ${user.firstName || "there"}`}
        description="Your signed-in PriceIQ profile and recent order history."
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
      <div className="grid gap-5 lg:grid-cols-[340px_1fr]">
        <section className="h-fit rounded-2xl border border-slate-200 bg-white p-5">
          <div className="grid h-14 w-14 place-items-center rounded-2xl bg-indigo-50 text-xl font-bold text-brand">
            {(user.firstName || "?")[0]}
            {(user.lastName || "")[0]}
          </div>
          <h2 className="mt-4 text-xl font-bold">
            {user.firstName} {user.lastName}
          </h2>
          <p className="mt-1 text-sm text-slate-500">{user.email}</p>
          <span className="mt-4 inline-block rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">
            {user.role || "USER"}
          </span>
          <div className="mt-5 border-t border-slate-100 pt-4 text-sm text-slate-500">
            User ID <b className="float-right text-slate-800">{user.userId}</b>
          </div>
          <Link
            to="/wishlist"
            className="mt-4 flex items-center gap-2 text-sm font-semibold text-brand"
          >
            <Heart size={16} /> Your wishlist <ArrowUpRight size={15} />
          </Link>
        </section>
        <section>
          <h2 className="mb-4 text-xl font-bold">Order history</h2>
          {error && <Notice error={error} />}
          {loading ? (
            <Loading />
          ) : orders.length === 0 ? (
            <Empty
              title="No orders to display"
              detail="Your order history endpoint returned no orders."
            />
          ) : (
            <div className="space-y-3">
              {orders.map((o, i) => (
                <Link
                  to={`/orders/${o.orderId}`}
                  key={o.orderId || i}
                  className="flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white p-5 transition hover:border-indigo-200 sm:flex-row sm:items-center sm:justify-between"
                >
                  <div>
                    <p className="font-bold">
                      {o.orderNumber || `Order #${o.orderId}`}
                    </p>
                    <p className="mt-1 text-xs text-slate-500">
                      {o.createdAt
                        ? new Date(o.createdAt).toLocaleDateString()
                        : "Date unavailable"}{" "}
                      · {o.items?.length || 0} line items
                    </p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold">
                      {o.orderStatus || "Unknown"}
                    </span>
                    <b>{money(o.totalAmount)}</b>
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
        detail="An authenticated user ID is required to create an order."
      />
    );
  if (order)
    return (
      <div className="mx-auto max-w-xl rounded-3xl border border-emerald-200 bg-white p-8 text-center">
        <div className="mx-auto grid h-14 w-14 place-items-center rounded-full bg-emerald-100 text-emerald-700">
          ✓
        </div>
        <h1 className="mt-4 text-2xl font-bold">Order created</h1>
        <p className="mt-2 text-slate-500">
          The backend returned order {order.orderNumber || `#${order.orderId}`}.
        </p>
        <p className="mt-4 text-2xl font-black">{money(order.totalAmount)}</p>
        <div className="mt-6 flex justify-center gap-3">
          <Button onClick={() => navigate(`/orders/${order.orderId}`)}>
            View order
          </Button>
          <Button variant="secondary" onClick={() => navigate("/")}>
            Continue shopping
          </Button>
        </div>
      </div>
    );
  const items = cart?.items || [];
  return (
    <>
      <PageTitle
        eyebrow="Checkout"
        title="Delivery details"
        description="This creates an order using the documented OrderRequest schema. Payment is a separate API operation."
      />
      {error && <Notice error={error} />}
      {items.length === 0 ? (
        <Empty
          title="Your cart is empty"
          detail="Add products to your cart before creating an order."
        />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
          <form
            onSubmit={submit}
            className="grid gap-4 rounded-2xl border border-slate-200 bg-white p-5 sm:grid-cols-2"
          >
            <Field
              label="Street address"
              name="shippingAddress"
              required
              value={form.shippingAddress}
              onChange={change}
              className="sm:col-span-2"
            />
            <Field
              label="City"
              name="city"
              required
              value={form.city}
              onChange={change}
            />
            <Field
              label="State"
              name="state"
              required
              value={form.state}
              onChange={change}
            />
            <Field
              label="Country"
              name="country"
              required
              value={form.country}
              onChange={change}
            />
            <Field
              label="Postal code"
              name="postalCode"
              required
              value={form.postalCode}
              onChange={change}
            />
            <Field
              label="Coupon code (optional)"
              name="couponCode"
              value={form.couponCode}
              onChange={change}
              className="sm:col-span-2"
            />
            <div className="sm:col-span-2">
              <Button type="submit" disabled={busy || items.length === 0}>
                {busy ? "Creating order…" : "Create order"}
              </Button>
            </div>
          </form>
          <aside className="h-fit rounded-2xl border border-slate-200 bg-white p-5">
            <h2 className="font-bold">Summary from cart</h2>
            {items.map((i) => (
              <div
                key={i.cartItemId}
                className="mt-3 flex justify-between gap-3 text-sm"
              >
                <span className="text-slate-500">
                  {i.productName} × {i.quantity}
                </span>
                <span className="font-semibold">{money(i.totalPrice)}</span>
              </div>
            ))}
            <div className="mt-4 flex justify-between border-t pt-4">
              <b>Total</b>
              <b className="text-lg">{money(cart?.totalAmount)}</b>
            </div>
          </aside>
        </div>
      )}
    </>
  );
}

export function OrderDetailPage() {
  const id = window.location.pathname.match(/\/orders\/(\d+)/)?.[1];
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  useEffect(() => {
    apiRoutes
      .order(id)
      .then((r) => setOrder(r.data))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, [id]);
  if (loading) return <Loading />;
  if (error) return <Notice error={error} />;
  if (!order) return <Empty title="Order not found" />;
  return (
    <>
      <PageTitle
        eyebrow="Order details"
        title={order.orderNumber || `Order #${order.orderId}`}
        description="Order information returned by the backend."
      />
      <div className="rounded-2xl border border-slate-200 bg-white p-6">
        <div className="flex flex-wrap justify-between gap-3">
          <span className="rounded-full bg-indigo-50 px-3 py-1 text-sm font-semibold text-brand">
            {order.orderStatus}
          </span>
          <b className="text-2xl">{money(order.totalAmount)}</b>
        </div>
        <p className="mt-5 text-sm leading-6 text-slate-600">
          {order.shippingAddress}, {order.city}, {order.state}, {order.country}{" "}
          {order.postalCode}
        </p>
        <h2 className="mt-7 font-bold">Items</h2>
        {(order.items || []).map((i, n) => (
          <div
            key={i.orderItemId || n}
            className="mt-3 flex justify-between border-b border-slate-100 py-3 text-sm"
          >
            <span>
              {i.productName} × {i.quantity}
            </span>
            <b>{money(i.totalPrice)}</b>
          </div>
        ))}
        <div className="mt-5 grid gap-3 text-sm sm:grid-cols-2">
          <p>
            Subtotal <b className="float-right">{money(order.subtotal)}</b>
          </p>
          <p>
            Shipping{" "}
            <b className="float-right">{money(order.shippingCharge)}</b>
          </p>
          <p>
            Tax <b className="float-right">{money(order.taxAmount)}</b>
          </p>
          <p>
            Discount{" "}
            <b className="float-right">{money(order.discountAmount)}</b>
          </p>
          {order.trackingNumber && (
            <p className="sm:col-span-2">
              Tracking number{" "}
              <b className="float-right">{order.trackingNumber}</b>
            </p>
          )}
        </div>
      </div>
      <PaymentForm order={order} />
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
    <section className="mt-6 max-w-2xl rounded-2xl border border-slate-200 bg-white p-6">
      <h2 className="text-lg font-bold">Process payment</h2>
      <p className="mt-1 text-sm text-slate-500">
        Submit the required payment fields. Use a transaction ID issued by your
        payment provider; this form does not initiate a real external payment.
      </p>
      {error && (
        <div className="mt-3">
          <Notice error={error} />
        </div>
      )}
      <form
        className="mt-4 grid gap-4 sm:grid-cols-2"
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
            else
              setError(
                "The backend response did not include paymentId. Response: " +
                  JSON.stringify(data),
              );
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
        <label className="block text-sm font-medium">
          Payment method
          <select
            className="mt-1 w-full rounded-xl border border-slate-200 p-3"
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
          >
            {["CARD", "UPI", "NET_BANKING", "WALLET", "CASH_ON_DELIVERY"].map(
              (v) => (
                <option key={v}>{v}</option>
              ),
            )}
          </select>
        </label>
        <Field
          label="Transaction ID"
          required
          value={transactionId}
          onChange={(e) => setTransactionId(e.target.value)}
          placeholder="Provider transaction reference"
        />
        <div className="sm:col-span-2">
          <Button type="submit" disabled={busy}>
            {busy ? "Submitting…" : "Submit payment request"}
          </Button>
        </div>
      </form>
    </section>
  );
}

export function PaymentDetailPage() {
  const id = window.location.pathname.match(/\/payments\/(\d+)/)?.[1];
  const [payment, setPayment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  useEffect(() => {
    apiRoutes
      .payment(id)
      .then((r) => setPayment(r.data))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, [id]);
  if (loading) return <Loading />;
  if (error) return <Notice error={error} />;
  if (!payment) return <Empty title="Payment not found" />;
  return (
    <>
      <PageTitle
        eyebrow="Payment"
        title={`Payment #${payment.paymentId}`}
        description="Payment details returned by the PriceIQ backend."
      />
      <div className="max-w-2xl rounded-2xl border border-slate-200 bg-white p-6">
        <span className="rounded-full bg-indigo-50 px-3 py-1 text-sm font-semibold text-brand">
          {payment.paymentStatus}
        </span>
        <div className="mt-5 space-y-4 text-sm">
          {[
            ["Order ID", payment.orderId],
            ["Transaction ID", payment.transactionId],
            ["Payment method", payment.paymentMethod],
            ["Amount", money(payment.amount)],
            ["Payment gateway", payment.paymentGateway],
            [
              "Created at",
              payment.createdAt
                ? new Date(payment.createdAt).toLocaleString()
                : "—",
            ],
          ].map(([k, v]) => (
            <div
              key={k}
              className="flex justify-between gap-4 border-b border-slate-100 pb-3"
            >
              <span className="text-slate-500">{k}</span>
              <b className="text-right">{v ?? "—"}</b>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}
