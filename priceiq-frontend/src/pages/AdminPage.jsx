import { useEffect, useState } from "react";
import {
  Activity,
  BarChart3,
  Box,
  CheckCircle2,
  DollarSign,
  Package,
  PlusCircle,
  ShoppingBag,
  Sparkles,
  TrendingUp,
  Users,
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

export function AdminPage() {
  const { user } = useAuth();
  const [analytics, setAnalytics] = useState(null);
  const [trending, setTrending] = useState(null);
  const [conversion, setConversion] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [form, setForm] = useState({
    name: "",
    description: "",
    sku: "",
    brand: "",
    category: "",
    basePrice: "",
    currentPrice: "",
    stockQuantity: "10",
    active: true,
    images: [],
    attributes: {},
  });
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    Promise.all([
      apiRoutes.analytics(),
      apiRoutes.trending(),
      apiRoutes.conversionRate(),
    ])
      .then(([a, t, c]) => {
        setAnalytics(a.data);
        setTrending(t.data);
        setConversion(c.data);
      })
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  }, []);

  async function create(e) {
    e.preventDefault();
    setCreating(true);
    setError("");
    setMessage("");
    try {
      const body = {
        ...form,
        basePrice: Number(form.basePrice),
        currentPrice: Number(form.currentPrice),
        stockQuantity: Number(form.stockQuantity),
      };
      const { data } = await apiRoutes.createProduct(body);
      setMessage(`Product "${data.name}" created successfully (ID #${data.id}).`);
      setForm({
        name: "",
        description: "",
        sku: "",
        brand: "",
        category: "",
        basePrice: "",
        currentPrice: "",
        stockQuantity: "10",
        active: true,
        images: [],
        attributes: {},
      });
    } catch (e) {
      setError(
        e.response?.data?.message || e.message || "Could not create product.",
      );
    } finally {
      setCreating(false);
    }
  }

  if (user?.role !== "ADMIN")
    return (
      <Empty
        title="Admin privileges required"
        detail="Only accounts with role ADMIN can view backend analytics and manage store catalog."
      />
    );

  const kpis = [
    {
      label: "Gross Revenue",
      value: money(analytics?.totalRevenue),
      icon: DollarSign,
      color: "text-emerald-600 bg-emerald-50",
    },
    {
      label: "Total Orders",
      value: analytics?.totalOrders ?? "—",
      icon: ShoppingBag,
      color: "text-brand bg-brand-50",
    },
    {
      label: "Active Users",
      value: analytics?.totalUsers ?? "—",
      icon: Users,
      color: "text-indigo-600 bg-indigo-50",
    },
    {
      label: "Total Products",
      value: analytics?.totalProducts ?? "—",
      icon: Package,
      color: "text-slate-700 bg-slate-100",
    },
    {
      label: "Kafka Events Tracked",
      value: analytics?.totalEvents ?? "—",
      icon: Activity,
      color: "text-amber-600 bg-amber-50",
    },
    {
      label: "Product Reviews",
      value: analytics?.totalReviews ?? "—",
      icon: Sparkles,
      color: "text-purple-600 bg-purple-50",
    },
  ];

  return (
    <>
      <PageTitle
        eyebrow="Admin Command Center"
        title="Store Analytics & Operations"
        description="Real-time metrics, conversion funnel, order status breakdown, and product catalog management."
      />

      {error && <Notice error={error} />}
      {message && (
        <div className="mb-6">
          <Notice success={message} onDismiss={() => setMessage("")} />
        </div>
      )}

      {loading ? (
        <Loading />
      ) : (
        <div className="space-y-8">
          {/* KPI Metrics Grid */}
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
            {kpis.map((k) => {
              const Icon = k.icon;
              return (
                <div
                  key={k.label}
                  className="rounded-3xl border border-slate-200/80 bg-white p-5 shadow-card"
                >
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold text-slate-500">
                      {k.label}
                    </span>
                    <div className={`grid h-8 w-8 place-items-center rounded-xl ${k.color}`}>
                      <Icon size={16} />
                    </div>
                  </div>
                  <p className="mt-3 text-2xl font-black tracking-tight text-ink">
                    {k.value}
                  </p>
                </div>
              );
            })}
          </div>

          {/* Section: Status Breakdown & Funnels */}
          <div className="grid gap-6 lg:grid-cols-3">
            {/* Orders By Status */}
            <section className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card lg:col-span-2">
              <div className="flex items-center gap-2">
                <BarChart3 size={18} className="text-brand" />
                <h2 className="text-base font-bold text-ink">Orders by Status</h2>
              </div>

              {analytics?.ordersByStatus &&
              Object.keys(analytics.ordersByStatus).length ? (
                <div className="mt-6 space-y-4">
                  {Object.entries(analytics.ordersByStatus).map(([status, count]) => {
                    const maxCount = Math.max(
                      1,
                      ...Object.values(analytics.ordersByStatus).map(Number),
                    );
                    const pct = Math.round((Number(count) / maxCount) * 100);
                    return (
                      <div key={status}>
                        <div className="mb-1.5 flex justify-between text-xs">
                          <span className="font-semibold text-slate-700">{status}</span>
                          <span className="font-bold text-slate-900">{count}</span>
                        </div>
                        <div className="h-2 w-full overflow-hidden rounded-full bg-slate-100">
                          <div
                            className="h-full rounded-full bg-brand transition-all duration-500"
                            style={{ width: `${pct}%` }}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              ) : (
                <p className="mt-4 text-xs text-slate-500">
                  No order status events recorded yet.
                </p>
              )}

              {/* Events By Type Pills */}
              <div className="mt-8 border-t border-slate-100 pt-6">
                <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                  Kafka Events Streamed by Type
                </h3>
                <div className="mt-3 flex flex-wrap gap-2">
                  {Object.entries(analytics?.eventsByType || {}).map(([evt, cnt]) => (
                    <span
                      key={evt}
                      className="inline-flex items-center gap-1.5 rounded-full border border-slate-200 bg-slate-50 px-3 py-1 text-xs text-slate-700"
                    >
                      <span className="h-1.5 w-1.5 rounded-full bg-brand" />
                      {evt}: <b className="text-ink">{cnt}</b>
                    </span>
                  ))}
                </div>
              </div>
            </section>

            {/* Conversion Rates */}
            <section className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
              <div className="flex items-center gap-2">
                <TrendingUp size={18} className="text-emerald-600" />
                <h2 className="text-base font-bold text-ink">Conversion Metrics</h2>
              </div>

              <div className="mt-6 space-y-3">
                {Object.entries(analytics?.conversionRates || {}).length ? (
                  Object.entries(analytics.conversionRates).map(([metric, val]) => (
                    <div
                      key={metric}
                      className="flex items-center justify-between rounded-2xl border border-slate-100 bg-slate-50/70 p-3 text-xs"
                    >
                      <span className="font-medium text-slate-600">{metric}</span>
                      <span className="font-bold text-brand">{val}</span>
                    </div>
                  ))
                ) : (
                  <p className="text-xs text-slate-400">
                    No conversion analytics computed yet.
                  </p>
                )}
              </div>

              {conversion && (
                <div className="mt-6 border-t border-slate-100 pt-4">
                  <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                    Raw Conversion Payload
                  </span>
                  <pre className="mt-2 max-h-40 overflow-auto rounded-xl bg-slate-900 p-3 font-mono text-[11px] text-emerald-400">
                    {JSON.stringify(conversion, null, 2)}
                  </pre>
                </div>
              )}
            </section>
          </div>

          {/* Section: Trending & Product Creation */}
          <div className="grid gap-6 lg:grid-cols-2">
            {/* Trending Items List */}
            <section className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Sparkles size={18} className="text-brand" />
                  <h2 className="text-base font-bold text-ink">Trending Products</h2>
                </div>
                <Badge variant="brand">Top Trends</Badge>
              </div>

              <div className="mt-5">
                {Array.isArray(trending) && trending.length > 0 ? (
                  <div className="divide-y divide-slate-100">
                    {trending.map((t, idx) => (
                      <div
                        key={t.productId || t.id || idx}
                        className="flex items-center justify-between py-3 text-xs"
                      >
                        <div className="flex items-center gap-3">
                          <span className="grid h-6 w-6 place-items-center rounded-lg bg-slate-100 font-bold text-slate-600 text-[10px]">
                            #{idx + 1}
                          </span>
                          <div>
                            <p className="font-bold text-slate-800">
                              {t.name || t.productName || `Product #${t.productId || t.id}`}
                            </p>
                            <p className="text-slate-400">
                              Views: {t.viewCount ?? "—"} · Carts: {t.cartCount ?? "—"}
                            </p>
                          </div>
                        </div>
                        <span className="font-bold text-brand">
                          Score: {t.trendScore != null ? Number(t.trendScore).toFixed(1) : "—"}
                        </span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <pre className="max-h-64 overflow-auto rounded-xl bg-slate-900 p-4 font-mono text-xs text-indigo-300">
                    {JSON.stringify(trending, null, 2)}
                  </pre>
                )}
              </div>
            </section>

            {/* Create Product Form */}
            <section className="rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
              <div className="flex items-center gap-2">
                <PlusCircle size={18} className="text-brand" />
                <h2 className="text-base font-bold text-ink">Add New Product</h2>
              </div>
              <p className="mt-1 text-xs text-slate-500">
                Create products conforming to PriceIQ backend specifications.
              </p>

              <form onSubmit={create} className="mt-5 grid gap-3 sm:grid-cols-2">
                <Field
                  label="Product Name"
                  required
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="e.g. MacBook Pro 16"
                  className="sm:col-span-2"
                />
                <Field
                  label="Description"
                  value={form.description}
                  onChange={(e) =>
                    setForm({ ...form, description: e.target.value })
                  }
                  placeholder="Specs, features, highlights…"
                  className="sm:col-span-2"
                />
                <Field
                  label="SKU"
                  required
                  value={form.sku}
                  onChange={(e) => setForm({ ...form, sku: e.target.value })}
                  placeholder="e.g. MBP-16-M3"
                />
                <Field
                  label="Brand"
                  value={form.brand}
                  onChange={(e) => setForm({ ...form, brand: e.target.value })}
                  placeholder="e.g. Apple"
                />
                <Field
                  label="Category"
                  value={form.category}
                  onChange={(e) =>
                    setForm({ ...form, category: e.target.value })
                  }
                  placeholder="e.g. Laptops"
                />
                <Field
                  label="Base Price (₹)"
                  type="number"
                  min="0"
                  required
                  value={form.basePrice}
                  onChange={(e) =>
                    setForm({ ...form, basePrice: e.target.value })
                  }
                  placeholder="e.g. 249900"
                />
                <Field
                  label="Current Price (₹)"
                  type="number"
                  min="0"
                  required
                  value={form.currentPrice}
                  onChange={(e) =>
                    setForm({ ...form, currentPrice: e.target.value })
                  }
                  placeholder="e.g. 229900"
                />
                <Field
                  label="Stock Quantity"
                  type="number"
                  min="0"
                  required
                  value={form.stockQuantity}
                  onChange={(e) =>
                    setForm({ ...form, stockQuantity: e.target.value })
                  }
                />

                <div className="sm:col-span-2 flex items-center justify-between pt-2">
                  <label className="flex items-center gap-2 text-xs font-semibold text-slate-700 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={form.active}
                      onChange={(e) =>
                        setForm({ ...form, active: e.target.checked })
                      }
                      className="rounded border-slate-300 text-brand focus:ring-brand"
                    />
                    Active and Available for Purchase
                  </label>
                  <Button
                    type="submit"
                    disabled={creating}
                    loading={creating}
                    className="shadow-glow"
                  >
                    Save Product
                  </Button>
                </div>
              </form>
            </section>
          </div>
        </div>
      )}
    </>
  );
}
