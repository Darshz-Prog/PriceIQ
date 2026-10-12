import { useEffect, useState } from "react";
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
    stockQuantity: "0",
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
      setMessage(`Product created: ${data.name} (ID ${data.id}).`);
      setForm({
        name: "",
        description: "",
        sku: "",
        brand: "",
        category: "",
        basePrice: "",
        currentPrice: "",
        stockQuantity: "0",
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
        title="Admin role required"
        detail="The analytics endpoints exist in the API contract, but the backend's per-endpoint role restrictions are not documented. This page is shown only to a session whose auth response has role ADMIN."
      />
    );
  return (
    <>
      <PageTitle
        eyebrow="Control room"
        title="Store overview"
        description="Live analytics from the documented PriceIQ analytics endpoints."
      />
      {error && <Notice error={error} />}
      {loading ? (
        <Loading />
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {[
              ["Total users", analytics?.totalUsers],
              ["Total orders", analytics?.totalOrders],
              ["Total products", analytics?.totalProducts],
              ["Tracked events", analytics?.totalEvents],
              [
                "Revenue",
                typeof analytics?.totalRevenue === "number"
                  ? new Intl.NumberFormat("en-IN", {
                      style: "currency",
                      currency: "INR",
                    }).format(analytics.totalRevenue)
                  : analytics?.totalRevenue,
              ],
              ["Reviews", analytics?.totalReviews],
            ].map(([label, value]) => (
              <div
                key={label}
                className="rounded-2xl border border-slate-200 bg-white p-5"
              >
                <p className="text-sm text-slate-500">{label}</p>
                <p className="mt-2 text-3xl font-black tracking-tight text-ink">
                  {value ?? "—"}
                </p>
              </div>
            ))}
          </div>
          <div className="mt-6 grid gap-5 lg:grid-cols-3">
            <section className="rounded-2xl border border-slate-200 bg-white p-5 lg:col-span-2">
              <h2 className="text-lg font-bold">Orders by status</h2>
              {analytics?.ordersByStatus &&
              Object.keys(analytics.ordersByStatus).length ? (
                Object.entries(analytics.ordersByStatus).map(([k, v]) => (
                  <div key={k} className="mt-4">
                    <div className="mb-1 flex justify-between text-sm">
                      <span>{k}</span>
                      <b>{v}</b>
                    </div>
                    <div className="h-2 rounded-full bg-slate-100">
                      <div
                        className="h-2 rounded-full bg-brand"
                        style={{
                          width: `${Math.min(100, (Number(v) / Math.max(1, ...Object.values(analytics.ordersByStatus).map(Number))) * 100)}%`,
                        }}
                      />
                    </div>
                  </div>
                ))
              ) : (
                <p className="mt-3 text-sm text-slate-500">
                  No order status breakdown returned.
                </p>
              )}
              <h2 className="mt-7 text-lg font-bold">Events by type</h2>
              <div className="mt-3 flex flex-wrap gap-2">
                {Object.entries(analytics?.eventsByType || {}).map(([k, v]) => (
                  <span
                    key={k}
                    className="rounded-full bg-slate-100 px-3 py-1.5 text-xs"
                  >
                    {k}: <b>{v}</b>
                  </span>
                ))}
              </div>
            </section>
            <section className="rounded-2xl border border-slate-200 bg-white p-5">
              <h2 className="text-lg font-bold">Conversion rates</h2>
              {Object.entries(analytics?.conversionRates || {}).length ? (
                Object.entries(analytics.conversionRates).map(([k, v]) => (
                  <div
                    key={k}
                    className="mt-3 flex justify-between gap-3 text-sm"
                  >
                    <span className="text-slate-500">{k}</span>
                    <b>{v}</b>
                  </div>
                ))
              ) : (
                <p className="mt-3 text-sm text-slate-500">
                  No conversion rates returned.
                </p>
              )}
              <h3 className="mt-6 font-bold">Conversion endpoint</h3>
              <pre className="mt-2 overflow-auto rounded-xl bg-slate-50 p-3 text-xs">
                {JSON.stringify(conversion, null, 2)}
              </pre>
            </section>
          </div>
          <div className="mt-6 grid gap-5 lg:grid-cols-2">
            <section className="rounded-2xl border border-slate-200 bg-white p-5">
              <h2 className="text-lg font-bold">Trending products</h2>
              <pre className="mt-3 overflow-auto rounded-xl bg-slate-50 p-4 text-xs leading-5">
                {JSON.stringify(trending, null, 2)}
              </pre>
            </section>
            <section className="rounded-2xl border border-slate-200 bg-white p-5">
              <h2 className="text-lg font-bold">Create product</h2>
              <p className="mt-1 text-sm text-slate-500">
                Uses the documented ProductRequest fields. Optional image upload
                can be used separately.
              </p>
              {message && (
                <p className="mt-3 text-sm text-emerald-700">{message}</p>
              )}
              <form
                onSubmit={create}
                className="mt-4 grid gap-3 sm:grid-cols-2"
              >
                {[
                  ["name", "Name"],
                  ["description", "Description"],
                  ["sku", "SKU"],
                  ["brand", "Brand"],
                  ["category", "Category"],
                  ["basePrice", "Base price"],
                  ["currentPrice", "Current price"],
                  ["stockQuantity", "Stock quantity"],
                ].map(([key, label]) => (
                  <Field
                    key={key}
                    label={label}
                    required={[
                      "name",
                      "basePrice",
                      "currentPrice",
                      "stockQuantity",
                    ].includes(key)}
                    type={
                      ["basePrice", "currentPrice", "stockQuantity"].includes(
                        key,
                      )
                        ? "number"
                        : "text"
                    }
                    min={
                      ["basePrice", "currentPrice", "stockQuantity"].includes(
                        key,
                      )
                        ? "0"
                        : undefined
                    }
                    value={form[key]}
                    onChange={(e) =>
                      setForm({ ...form, [key]: e.target.value })
                    }
                    className={key === "description" ? "sm:col-span-2" : ""}
                  />
                ))}
                <label className="flex items-center gap-2 text-sm">
                  <input
                    type="checkbox"
                    checked={form.active}
                    onChange={(e) =>
                      setForm({ ...form, active: e.target.checked })
                    }
                  />{" "}
                  Product active
                </label>
                <div className="sm:col-span-2">
                  <Button type="submit" disabled={creating}>
                    {creating ? "Creating…" : "Create product"}
                  </Button>
                </div>
              </form>
            </section>
          </div>
        </>
      )}
    </>
  );
}
