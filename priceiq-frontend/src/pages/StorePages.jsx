import { useEffect, useState } from "react";
import {
  Link,
  useNavigate,
  useParams,
  useSearchParams,
} from "react-router-dom";
import {
  ArrowRight,
  Heart,
  ShoppingBag,
  SlidersHorizontal,
  Star,
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

function money(value) {
  return typeof value === "number"
    ? new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2,
      }).format(value)
    : "—";
}
function productImage(p) {
  return (
    p?.images?.find((i) => i.primaryImage)?.imageUrl ||
    p?.images?.[0]?.imageUrl ||
    ""
  );
}
function ProductCard({ product, onCart, onWish }) {
  const img = productImage(product);
  return (
    <article className="group overflow-hidden rounded-2xl border border-slate-200/80 bg-white transition hover:-translate-y-1 hover:shadow-soft">
      <Link
        to={`/products/${product.id}`}
        className="relative block aspect-[4/3] overflow-hidden bg-slate-100"
      >
        {img ? (
          <img
            src={img}
            alt={
              product.images?.find((i) => i.primaryImage)?.altText ||
              product.name
            }
            className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
          />
        ) : (
          <div className="grid h-full place-items-center text-sm text-slate-400">
            No product image
          </div>
        )}
        {product.active === false && (
          <span className="absolute left-3 top-3 rounded-full bg-slate-900/80 px-2.5 py-1 text-xs text-white">
            Unavailable
          </span>
        )}
      </Link>
      <div className="p-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-brand">
          {product.brand || product.category || "PriceIQ pick"}
        </p>
        <Link to={`/products/${product.id}`}>
          <h3 className="mt-1 line-clamp-2 min-h-10 font-semibold text-slate-800 hover:text-brand">
            {product.name}
          </h3>
        </Link>
        <div className="mt-2 flex items-center gap-1 text-xs text-amber-500">
          <Star size={14} fill="currentColor" />
          <span className="font-semibold text-slate-700">
            {product.averageRating ?? "—"}
          </span>
          <span className="text-slate-400">({product.totalReviews ?? 0})</span>
        </div>
        <div className="mt-3 flex items-center justify-between gap-2">
          <span className="text-lg font-bold text-ink">
            {money(product.currentPrice)}
          </span>
          <div className="flex gap-1">
            <button
              title="Add to wishlist"
              onClick={() => onWish(product)}
              className="rounded-lg border border-slate-200 p-2 text-slate-500 hover:border-pink-200 hover:text-pink-600"
            >
              <Heart size={17} />
            </button>
            <button
              title="Add to cart"
              onClick={() => onCart(product)}
              disabled={product.active === false || product.stockQuantity === 0}
              className="rounded-lg bg-brand p-2 text-white hover:bg-indigo-700 disabled:opacity-40"
            >
              <ShoppingBag size={17} />
            </button>
          </div>
        </div>
      </div>
    </article>
  );
}

export function ProductsPage({ searchMode = false, feedMode = false }) {
  const { user } = useAuth();
  const [params, setParams] = useSearchParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [filters, setFilters] = useState({
    category: "",
    brand: "",
    minPrice: "",
    maxPrice: "",
    sortBy: "",
  });
  const page = Number(params.get("page") || 0);
  const keyword = params.get("keyword") || "";
  async function load() {
    setLoading(true);
    setError("");
    try {
      const response = feedMode
        ? await apiRoutes.feed()
        : searchMode
          ? await apiRoutes.searchProducts({ keyword, page, size: 12 })
          : await apiRoutes.products({
              page,
              size: 12,
              ...Object.fromEntries(
                Object.entries(filters).filter(([, v]) => v !== ""),
              ),
            });
      setData(response.data);
    } catch (e) {
      setError(
        e.response?.data?.message ||
          e.message ||
          "Could not load products. Check that the backend is running.",
      );
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    load();
  }, [page, keyword, searchMode, feedMode]);
  async function addCart(p) {
    if (!user) {
      setNotice("Sign in to add products to your cart.");
      return;
    }
    try {
      await apiRoutes.addToCart({
        userId: user.userId,
        productId: p.id,
        quantity: 1,
      });
      setNotice(`${p.name} was added to your cart.`);
    } catch (e) {
      setNotice(
        e.response?.data?.message || "Could not add this item to the cart.",
      );
    }
  }
  async function addWish(p) {
    if (!user) {
      setNotice("Sign in to save products to your wishlist.");
      return;
    }
    try {
      await apiRoutes.addWishlist({ userId: user.userId, productId: p.id });
      setNotice(`${p.name} was added to your wishlist.`);
    } catch (e) {
      setNotice(
        e.response?.data?.message || "Could not add this item to the wishlist.",
      );
    }
  }
  const products = Array.isArray(data) ? data : data?.content || [];
  const pages = data?.totalPages ?? 1;
  return (
    <>
      <section className="relative mb-10 overflow-hidden rounded-3xl bg-ink px-6 py-9 text-white sm:px-10 sm:py-12">
        <div className="absolute -right-10 -top-20 h-64 w-64 rounded-full bg-brand/40 blur-3xl" />
        <div className="relative max-w-2xl">
          <span className="inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/10 px-3 py-1.5 text-xs font-semibold text-indigo-100">
            <span className="h-1.5 w-1.5 rounded-full bg-emerald-300" /> A
            smarter way to shop
          </span>
          <h1 className="mt-5 text-4xl font-black tracking-tight sm:text-5xl">
            Good finds.
            <br />
            <span className="text-indigo-300">Better decisions.</span>
          </h1>
          <p className="mt-4 max-w-lg text-sm leading-6 text-slate-300">
            Discover products and explore recommendations powered by your
            PriceIQ storefront.
          </p>
          <div className="mt-6 flex flex-wrap gap-3">
            <a
              href="#catalog"
              className="inline-flex items-center gap-2 rounded-xl bg-white px-4 py-3 text-sm font-bold text-ink hover:bg-indigo-50"
            >
              Explore products <ArrowRight size={17} />
            </a>
            {user && (
              <Link
                to="/account"
                className="rounded-xl border border-white/20 px-4 py-3 text-sm font-semibold hover:bg-white/10"
              >
                Your account
              </Link>
            )}
          </div>
        </div>
      </section>
      <div id="catalog">
        <PageTitle
          eyebrow={feedMode ? "Picked for you" : "The catalog"}
          title={
            searchMode
              ? `Results for “${keyword}”`
              : feedMode
                ? "Recommended for you"
                : "Explore products"
          }
          description={
            searchMode
              ? "Search results returned by your PriceIQ backend."
              : feedMode
                ? "Recommendations are loaded directly from the recommendation feed endpoint."
                : "Find your next favourite from products available in the backend."
          }
        />
      </div>
      {notice && (
        <div className="mb-4 flex items-center justify-between rounded-xl bg-indigo-50 p-3 text-sm text-indigo-800">
          {notice}
          <button onClick={() => setNotice("")} className="font-bold">
            ×
          </button>
        </div>
      )}
      {!searchMode && !feedMode && (
        <form
          onSubmit={(e) => {
            e.preventDefault();
            setParams({ page: "0" });
            load();
          }}
          className="mb-6 grid grid-cols-2 gap-3 rounded-2xl border border-slate-200 bg-white p-4 sm:grid-cols-3 lg:grid-cols-6"
        >
          <div className="col-span-2 flex items-center gap-2 text-sm font-semibold text-slate-700 lg:col-span-1">
            <SlidersHorizontal size={17} /> Filter products
          </div>
          <Field
            placeholder="Category"
            aria-label="Category"
            value={filters.category}
            onChange={(e) =>
              setFilters({ ...filters, category: e.target.value })
            }
          />
          <Field
            placeholder="Brand"
            aria-label="Brand"
            value={filters.brand}
            onChange={(e) => setFilters({ ...filters, brand: e.target.value })}
          />
          <Field
            type="number"
            min="0"
            placeholder="Min price"
            aria-label="Minimum price"
            value={filters.minPrice}
            onChange={(e) =>
              setFilters({ ...filters, minPrice: e.target.value })
            }
          />
          <Field
            type="number"
            min="0"
            placeholder="Max price"
            aria-label="Maximum price"
            value={filters.maxPrice}
            onChange={(e) =>
              setFilters({ ...filters, maxPrice: e.target.value })
            }
          />
          <div className="col-span-2 flex gap-2 sm:col-span-1">
            <select
              aria-label="Sort by"
              className="min-w-0 flex-1 rounded-xl border border-slate-200 px-2 text-sm"
              value={filters.sortBy}
              onChange={(e) =>
                setFilters({ ...filters, sortBy: e.target.value })
              }
            >
              <option value="">Sort by…</option>
              <option value="currentPrice">Current price</option>
              <option value="name">Name</option>
            </select>
            <Button type="submit">Apply</Button>
          </div>
        </form>
      )}
      {error ? (
        <Notice error={error} />
      ) : loading ? (
        <Loading />
      ) : products.length === 0 ? (
        <Empty
          title="No products found"
          detail="The API returned no products for this request."
        />
      ) : (
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {products.map((p) => (
            <ProductCard
              key={p.id}
              product={p}
              onCart={addCart}
              onWish={addWish}
            />
          ))}
        </div>
      )}
      {!loading && !error && !Array.isArray(data) && data?.totalPages > 1 && (
        <div className="mt-8 flex items-center justify-center gap-3">
          <Button
            variant="secondary"
            disabled={page <= 0}
            onClick={() =>
              setParams({
                page: String(page - 1),
                ...(keyword ? { keyword } : {}),
              })
            }
          >
            Previous
          </Button>
          <span className="text-sm text-slate-500">
            Page {page + 1} of {pages}
          </span>
          <Button
            variant="secondary"
            disabled={page + 1 >= pages}
            onClick={() =>
              setParams({
                page: String(page + 1),
                ...(keyword ? { keyword } : {}),
              })
            }
          >
            Next
          </Button>
        </div>
      )}
    </>
  );
}

export function ProductDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  useEffect(() => {
    if (!id || !/^\d+$/.test(id)) {
      setError(`Invalid product ID: ${id || "(missing)"}`);
      setLoading(false);
      return;
    }

    let alive = true;

    Promise.all([
      apiRoutes.product(id),
      apiRoutes.reviews(id).catch(() => ({ data: [] })),
    ])
      .then(([p, r]) => {
        if (alive) {
          setProduct(p.data);
          setReviews(Array.isArray(r.data) ? r.data : []);
        }
      })
      .catch((e) => {
        if (alive) {
          setError(
            e.response?.data?.message || e.message || "Could not load product.",
          );
        }
      })
      .finally(() => {
        if (alive) setLoading(false);
      });

    return () => {
      alive = false;
    };
  }, [id]);
  async function add(kind) {
    if (!user) {
      setNotice("Please sign in first.");
      return;
    }
    try {
      if (kind === "cart")
        await apiRoutes.addToCart({
          userId: user.userId,
          productId: product.id,
          quantity: 1,
        });
      else
        await apiRoutes.addWishlist({
          userId: user.userId,
          productId: product.id,
        });
      setNotice(kind === "cart" ? "Added to cart." : "Added to wishlist.");
    } catch (e) {
      setNotice(e.response?.data?.message || "Request failed.");
    }
  }
  async function submitReview(e) {
    e.preventDefault();
    if (!user) {
      setNotice("Please sign in to write a review.");
      return;
    }
    try {
      const { data } = await apiRoutes.createReview({
        userId: user.userId,
        productId: Number(id),
        rating,
        comment: comment || undefined,
      });
      setReviews((old) => [data, ...old]);
      setComment("");
      setNotice("Review submitted.");
    } catch (e) {
      setNotice(e.response?.data?.message || "Could not submit review.");
    }
  }
  if (loading) return <Loading />;
  if (error) return <Notice error={error} />;
  if (!product) return <Empty title="Product not found" />;
  const img =
    product.images?.find((i) => i.primaryImage)?.imageUrl ||
    product.images?.[0]?.imageUrl;
  return (
    <>
      <div className="mb-5 text-sm text-slate-500">
        <Link to="/" className="hover:text-brand">
          Home
        </Link>{" "}
        / {product.category || "Product"} /{" "}
        <span className="text-slate-800">{product.name}</span>
      </div>
      <div className="grid gap-8 lg:grid-cols-2">
        <div className="grid min-h-80 place-items-center overflow-hidden rounded-3xl bg-slate-100">
          {img ? (
            <img
              src={img}
              alt={product.name}
              className="max-h-[520px] w-full object-contain"
            />
          ) : (
            <span className="text-slate-400">No product image available</span>
          )}
        </div>
        <div className="py-2">
          <p className="text-sm font-semibold uppercase tracking-widest text-brand">
            {product.brand || product.category}
          </p>
          <h1 className="mt-3 text-3xl font-bold tracking-tight text-ink sm:text-4xl">
            {product.name}
          </h1>
          <div className="mt-3 flex items-center gap-2 text-sm text-slate-500">
            <Star size={16} fill="#f59e0b" className="text-amber-500" />
            <b className="text-slate-800">
              {product.averageRating ?? "—"}
            </b> · {product.totalReviews ?? 0} reviews · SKU{" "}
            {product.sku || "—"}
          </div>
          <div className="mt-7 text-3xl font-black text-ink">
            {money(product.currentPrice)}
          </div>
          {product.basePrice != null &&
            product.basePrice !== product.currentPrice && (
              <p className="mt-1 text-sm text-slate-400">
                Base price {money(product.basePrice)}
              </p>
            )}
          <p className="mt-5 leading-7 text-slate-600">
            {product.description || "No description provided by the backend."}
          </p>
          <p className="mt-4 text-sm text-slate-500">
            {product.stockQuantity ?? "—"} in stock
          </p>
          <div className="mt-6 flex flex-wrap gap-3">
            <Button
              onClick={() => add("cart")}
              disabled={!product.active || product.stockQuantity === 0}
            >
              <ShoppingBag size={17} /> Add to cart
            </Button>
            <Button variant="secondary" onClick={() => add("wishlist")}>
              <Heart size={17} /> Save item
            </Button>
          </div>
          {notice && <p className="mt-3 text-sm text-indigo-700">{notice}</p>}
          {product.attributes && Object.keys(product.attributes).length > 0 && (
            <div className="mt-7 rounded-2xl border border-slate-200 p-4">
              <h3 className="font-semibold">Product details</h3>
              {Object.entries(product.attributes).map(([k, v]) => (
                <p key={k} className="mt-2 text-sm text-slate-600">
                  <b>{k}:</b> {v}
                </p>
              ))}
            </div>
          )}
        </div>
      </div>
      <section className="mt-12">
        <PageTitle
          title="Customer reviews"
          description="Reviews returned by the product reviews endpoint."
        />
        <div className="grid gap-5 lg:grid-cols-2">
          <div className="space-y-3">
            {reviews.length ? (
              reviews.map((r, i) => (
                <article
                  key={r.reviewId || i}
                  className="rounded-2xl border border-slate-200 bg-white p-5"
                >
                  <div className="flex justify-between gap-3">
                    <b>{r.userName || "Customer"}</b>
                    <span className="text-amber-500">
                      {"★".repeat(Math.max(0, Math.min(5, r.rating || 0)))}
                    </span>
                  </div>
                  <p className="mt-2 text-sm leading-6 text-slate-600">
                    {r.comment || "No written comment."}
                  </p>
                  <p className="mt-3 text-xs text-slate-400">
                    {r.createdAt
                      ? new Date(r.createdAt).toLocaleDateString()
                      : ""}
                    {r.verifiedPurchase ? " · Verified purchase" : ""}
                  </p>
                </article>
              ))
            ) : (
              <Empty
                title="No reviews to show"
                detail="The API returned no review list for this product."
              />
            )}
          </div>
          <form
            onSubmit={submitReview}
            className="h-fit rounded-2xl border border-slate-200 bg-white p-5"
          >
            <h3 className="text-lg font-bold">Write a review</h3>
            <p className="mt-1 text-sm text-slate-500">
              Your rating must be between 1 and 5.
            </p>
            <label className="mt-4 block text-sm font-medium">
              Rating
              <select
                value={rating}
                onChange={(e) => setRating(Number(e.target.value))}
                className="mt-1 w-full rounded-xl border border-slate-200 p-3"
              >
                {[5, 4, 3, 2, 1].map((n) => (
                  <option key={n} value={n}>
                    {n} / 5
                  </option>
                ))}
              </select>
            </label>
            <label className="mt-4 block text-sm font-medium">
              Comment (optional)
              <textarea
                rows="4"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                className="mt-1 w-full rounded-xl border border-slate-200 p-3 text-sm outline-none focus:border-brand"
                placeholder="Share your experience…"
              />
            </label>
            <Button type="submit" className="mt-4">
              Submit review
            </Button>
          </form>
        </div>
      </section>
    </>
  );
}
