import { useEffect, useRef, useState } from "react";
import {
  Link,
  useNavigate,
  useParams,
  useSearchParams,
} from "react-router-dom";
import {
  ArrowRight,
  ChevronLeft,
  ChevronRight,
  Flame,
  Heart,
  Plus,
  ShoppingBag,
  SlidersHorizontal,
  Sparkles,
  Star,
  Tag,
  TrendingUp,
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
  RatingStars,
} from "../components/UI";

function money(value) {
  return typeof value === "number"
    ? new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 0,
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

function calculateDiscount(base, current) {
  if (!base || !current || base <= current) return null;
  return Math.round(((base - current) / base) * 100);
}

export function ProductCard({ product, onCart, onWish }) {
  const img = productImage(product);
  const discount = calculateDiscount(product.basePrice, product.currentPrice);

  return (
    <article className="group relative flex flex-col overflow-hidden rounded-2xl border border-slate-200/70 bg-white transition-all duration-300 hover:-translate-y-1 hover:border-slate-300 hover:shadow-hover">
      {/* Product Image Box */}
      <Link
        to={`/products/${product.id}`}
        className="relative block aspect-[4/3] w-full overflow-hidden bg-slate-50"
      >
        {img ? (
          <img
            src={img}
            alt={
              product.images?.find((i) => i.primaryImage)?.altText ||
              product.name
            }
            className="h-full w-full object-cover transition-transform duration-500 ease-out group-hover:scale-105"
            loading="lazy"
          />
        ) : (
          <div className="grid h-full place-items-center text-xs text-slate-400">
            No image
          </div>
        )}

        {/* Badges Overlay */}
        <div className="absolute left-3 top-3 flex flex-col gap-1.5">
          {discount && (
            <span className="rounded-lg bg-rose-600 px-2 py-0.5 text-[11px] font-bold text-white shadow-sm">
              {discount}% OFF
            </span>
          )}
          {product.active === false && (
            <span className="rounded-lg bg-slate-900/80 px-2 py-0.5 text-[11px] font-medium text-white backdrop-blur-sm">
              Unavailable
            </span>
          )}
        </div>

        {/* Wishlist Quick Action */}
        <button
          title="Save to wishlist"
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            onWish(product);
          }}
          className="absolute right-3 top-3 grid h-8 w-8 place-items-center rounded-xl bg-white/90 text-slate-500 shadow-sm backdrop-blur-sm transition hover:bg-white hover:text-rose-600 active:scale-90"
        >
          <Heart size={16} />
        </button>
      </Link>

      {/* Card Body */}
      <div className="flex flex-1 flex-col p-4">
        <div className="flex items-center justify-between text-xs">
          <span className="font-semibold uppercase tracking-wider text-brand">
            {product.brand || product.category || "PriceIQ"}
          </span>
          {product.stockQuantity != null && product.stockQuantity <= 5 && product.stockQuantity > 0 && (
            <span className="text-[11px] font-medium text-amber-600">
              Only {product.stockQuantity} left
            </span>
          )}
        </div>

        <Link to={`/products/${product.id}`}>
          <h3 className="mt-1 line-clamp-2 min-h-[2.5rem] text-sm font-semibold text-slate-800 transition hover:text-brand">
            {product.name}
          </h3>
        </Link>

        {/* Rating */}
        <div className="mt-2 flex items-center gap-1.5">
          <RatingStars
            rating={product.averageRating}
            totalReviews={product.totalReviews}
            size={13}
          />
        </div>

        {/* Price & Action */}
        <div className="mt-auto pt-3 flex items-center justify-between gap-2 border-t border-slate-100">
          <div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-base font-bold text-ink">
                {money(product.currentPrice)}
              </span>
              {discount && (
                <span className="text-xs text-slate-400 line-through">
                  {money(product.basePrice)}
                </span>
              )}
            </div>
          </div>

          <button
            title="Add to cart"
            onClick={() => onCart(product)}
            disabled={product.active === false || product.stockQuantity === 0}
            className="flex items-center gap-1.5 rounded-xl bg-slate-900 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-brand active:scale-95 disabled:opacity-40 disabled:hover:bg-slate-900"
          >
            <ShoppingBag size={14} /> Add
          </button>
        </div>
      </div>
    </article>
  );
}

/**
 * Modern horizontal product rail with left/right navigation arrows
 */
export function MiniProductRail({
  title,
  description,
  products,
  onCart,
  onWish,
  badgeIcon: BadgeIcon = Sparkles,
}) {
  const scrollRef = useRef(null);

  if (!products || products.length === 0) return null;

  const scroll = (direction) => {
    if (!scrollRef.current) return;
    const distance = direction === "left" ? -280 : 280;
    scrollRef.current.scrollBy({ left: distance, behavior: "smooth" });
  };

  return (
    <section className="my-8">
      <div className="mb-4 flex items-end justify-between">
        <div>
          {description && (
            <p className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider text-brand">
              <BadgeIcon size={13} />
              {description}
            </p>
          )}
          <h2 className="mt-0.5 text-xl font-bold tracking-tight text-ink sm:text-2xl">
            {title}
          </h2>
        </div>

        {/* Scroll Controls */}
        <div className="flex items-center gap-1.5">
          <button
            onClick={() => scroll("left")}
            aria-label="Scroll left"
            className="grid h-8 w-8 place-items-center rounded-xl border border-slate-200 bg-white text-slate-600 shadow-sm transition hover:bg-slate-50 active:scale-90"
          >
            <ChevronLeft size={16} />
          </button>
          <button
            onClick={() => scroll("right")}
            aria-label="Scroll right"
            className="grid h-8 w-8 place-items-center rounded-xl border border-slate-200 bg-white text-slate-600 shadow-sm transition hover:bg-slate-50 active:scale-90"
          >
            <ChevronRight size={16} />
          </button>
        </div>
      </div>

      <div
        ref={scrollRef}
        className="flex gap-4 overflow-x-auto pb-3 pt-1 scrollbar-none"
        style={{ scrollbarWidth: "none" }}
      >
        {products.map((p) => {
          const img = productImage(p);
          const discount = calculateDiscount(p.basePrice, p.currentPrice);
          return (
            <article
              key={p.id}
              className="group flex w-48 shrink-0 flex-col overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-card transition-all duration-200 hover:-translate-y-1 hover:border-slate-300 hover:shadow-hover"
            >
              <Link
                to={`/products/${p.id}`}
                className="relative block aspect-[4/3] w-full overflow-hidden bg-slate-50"
              >
                {img ? (
                  <img
                    src={img}
                    alt={p.name}
                    className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
                    loading="lazy"
                  />
                ) : (
                  <div className="grid h-full place-items-center text-xs text-slate-400">
                    No image
                  </div>
                )}
                {discount && (
                  <span className="absolute left-2.5 top-2.5 rounded-md bg-rose-600 px-1.5 py-0.5 text-[10px] font-bold text-white shadow-sm">
                    {discount}%
                  </span>
                )}
              </Link>
              <div className="flex flex-1 flex-col p-3">
                <Link to={`/products/${p.id}`}>
                  <p className="line-clamp-2 min-h-[2.25rem] text-xs font-semibold leading-snug text-slate-800 transition hover:text-brand">
                    {p.name}
                  </p>
                </Link>
                <div className="mt-1 flex items-baseline gap-1">
                  <span className="text-xs font-bold text-ink">
                    {money(p.currentPrice)}
                  </span>
                  {discount && (
                    <span className="text-[10px] text-slate-400 line-through">
                      {money(p.basePrice)}
                    </span>
                  )}
                </div>
                <div className="mt-3 flex gap-1.5 pt-2 border-t border-slate-100">
                  <button
                    title="Add to wishlist"
                    onClick={() => onWish(p)}
                    className="grid h-7 w-7 place-items-center rounded-lg border border-slate-200 text-slate-500 transition hover:border-rose-200 hover:text-rose-600 active:scale-95"
                  >
                    <Heart size={13} />
                  </button>
                  <button
                    title="Add to cart"
                    onClick={() => onCart(p)}
                    disabled={p.active === false || p.stockQuantity === 0}
                    className="flex-1 rounded-lg bg-slate-900 py-1 text-xs font-semibold text-white transition hover:bg-brand active:scale-95 disabled:opacity-40"
                  >
                    Add
                  </button>
                </div>
              </div>
            </article>
          );
        })}
      </div>
    </section>
  );
}

/**
 * Frequently Bought Together interactive bundle card
 */
function FbtBundleCard({ sourceProduct, fbtProduct, onBuyBundle }) {
  if (!fbtProduct) return null;
  const totalPrice = (sourceProduct?.currentPrice || 0) + (fbtProduct?.currentPrice || 0);

  return (
    <section className="my-8 rounded-3xl border border-brand-100 bg-gradient-to-r from-brand-50/60 to-indigo-50/30 p-6 shadow-sm">
      <div className="mb-4 flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-brand">
        <Sparkles size={14} /> Frequently Bought Together Bundle
      </div>
      <div className="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex flex-wrap items-center gap-4">
          {/* Main Item */}
          <div className="flex items-center gap-3 rounded-2xl bg-white p-3 border border-slate-200/80 shadow-sm">
            <img
              src={productImage(sourceProduct)}
              alt={sourceProduct.name}
              className="h-16 w-16 rounded-xl object-cover bg-slate-100"
            />
            <div className="max-w-[180px]">
              <span className="text-[10px] font-bold text-slate-400 uppercase">This item</span>
              <p className="line-clamp-1 text-xs font-semibold text-slate-800">{sourceProduct.name}</p>
              <p className="text-xs font-bold text-brand">{money(sourceProduct.currentPrice)}</p>
            </div>
          </div>

          <div className="grid h-8 w-8 place-items-center rounded-full bg-white text-slate-500 shadow-sm border border-slate-200">
            <Plus size={16} />
          </div>

          {/* FBT Item */}
          <Link
            to={`/products/${fbtProduct.id}`}
            className="flex items-center gap-3 rounded-2xl bg-white p-3 border border-slate-200/80 shadow-sm transition hover:border-brand-200"
          >
            <img
              src={productImage(fbtProduct)}
              alt={fbtProduct.name}
              className="h-16 w-16 rounded-xl object-cover bg-slate-100"
            />
            <div className="max-w-[180px]">
              <span className="text-[10px] font-bold text-brand uppercase">Pairs great with</span>
              <p className="line-clamp-1 text-xs font-semibold text-slate-800 hover:text-brand">
                {fbtProduct.name}
              </p>
              <p className="text-xs font-bold text-brand">{money(fbtProduct.currentPrice)}</p>
            </div>
          </Link>
        </div>

        {/* Total & Action */}
        <div className="flex items-center justify-between gap-4 border-t border-slate-200/60 pt-4 lg:border-t-0 lg:pt-0">
          <div>
            <span className="text-xs text-slate-500">Combined Total:</span>
            <div className="text-xl font-black text-ink">{money(totalPrice)}</div>
          </div>
          <Button onClick={onBuyBundle} className="shadow-glow">
            <ShoppingBag size={16} /> Add Both to Cart
          </Button>
        </div>
      </div>
    </section>
  );
}

export function ProductsPage({ searchMode = false, feedMode = false }) {
  const { user } = useAuth();
  const [params, setParams] = useSearchParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [trending, setTrending] = useState([]);
  const [filters, setFilters] = useState({
    category: "",
    brand: "",
    minPrice: "",
    maxPrice: "",
    sortBy: "",
  });

  const page = Number(params.get("page") || 0);
  const keyword = params.get("keyword") || "";

  // Category quick filter chips
  const quickCategories = [
    "All",
    "Smartphones",
    "Laptops",
    "Audio",
    "Accessories",
    "Wearables",
  ];

  // Load trending items on catalog homepage
  useEffect(() => {
    if (!searchMode && !feedMode) {
      apiRoutes
        .trendingRecommendations(8)
        .then((r) => setTrending(Array.isArray(r.data) ? r.data : []))
        .catch(() => {});
    }
  }, [searchMode, feedMode]);

  async function load() {
    setLoading(true);
    setError("");
    try {
      if (feedMode && !user) {
        // If not logged in, fetch trending products as friendly guest feed
        const trendingRes = await apiRoutes.trendingRecommendations(12);
        setData({ content: trendingRes.data, totalPages: 1, guestFeed: true });
        return;
      }

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

      // Track search event if user entered a keyword
      if (searchMode && keyword.trim()) {
        apiRoutes
          .trackEvent({
            userId: user?.userId,
            searchKeyword: keyword.trim(),
            eventType: "SEARCH",
          })
          .catch(() => {});
      }
    } catch (e) {
      setError(
        e.response?.data?.message ||
          e.message ||
          "Could not load products. Please check backend connection.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [page, keyword, searchMode, feedMode, user]);

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
      apiRoutes
        .trackEvent({
          userId: user.userId,
          productId: p.id,
          eventType: "ADD_TO_CART",
        })
        .catch(() => {});
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
      apiRoutes
        .trackEvent({
          userId: user.userId,
          productId: p.id,
          eventType: "ADD_TO_WISHLIST",
        })
        .catch(() => {});
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
      {/* Hero Section (only on main catalog page) */}
      {!searchMode && !feedMode && (
        <section className="relative mb-10 overflow-hidden rounded-3xl bg-ink px-6 py-10 text-white sm:px-10 sm:py-14 shadow-soft">
          <div className="pointer-events-none absolute -right-20 -top-24 h-80 w-80 rounded-full bg-brand/30 blur-3xl" />
          <div className="pointer-events-none absolute -left-20 -bottom-24 h-80 w-80 rounded-full bg-accent-violet/20 blur-3xl" />
          <div className="relative max-w-2xl">
            <span className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3.5 py-1.5 text-xs font-semibold text-indigo-200 backdrop-blur-md">
              <span className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse" />
              Hybrid Recommendation Engine Active
            </span>
            <h1 className="mt-5 text-4xl font-extrabold tracking-tight sm:text-5xl lg:text-6xl">
              Intelligent picks.
              <br />
              <span className="bg-gradient-to-r from-indigo-300 via-indigo-200 to-white bg-clip-text text-transparent">
                Exceptional value.
              </span>
            </h1>
            <p className="mt-4 max-w-lg text-sm leading-relaxed text-slate-300 sm:text-base">
              Explore dynamic pricing, personalized hybrid feeds, and real-time Kafka event scoring tailored to you.
            </p>
            <div className="mt-7 flex flex-wrap gap-3">
              <a
                href="#catalog"
                className="inline-flex items-center gap-2 rounded-xl bg-white px-5 py-3 text-sm font-bold text-ink shadow-sm transition hover:bg-slate-100 active:scale-95"
              >
                Explore Products <ArrowRight size={16} />
              </a>
              <Link
                to="/recommendations"
                className="inline-flex items-center gap-2 rounded-xl border border-white/20 bg-white/5 px-5 py-3 text-sm font-semibold text-white backdrop-blur-sm transition hover:bg-white/10 active:scale-95"
              >
                <Sparkles size={16} className="text-brand-100" /> Personalized Feed
              </Link>
            </div>
          </div>
        </section>
      )}

      {/* Personalized Feed Banner */}
      {feedMode && (
        <section className="relative mb-10 overflow-hidden rounded-3xl bg-gradient-to-br from-brand to-indigo-900 p-8 text-white sm:p-12 shadow-soft">
          <div className="pointer-events-none absolute -right-10 -top-10 h-64 w-64 rounded-full bg-white/10 blur-2xl" />
          <div className="relative max-w-xl">
            <span className="inline-flex items-center gap-1.5 rounded-full bg-white/15 px-3 py-1 text-xs font-semibold text-indigo-100 backdrop-blur-sm">
              <Sparkles size={14} /> AI Recommendation Feed
            </span>
            <h1 className="mt-3 text-3xl font-extrabold tracking-tight sm:text-4xl">
              Curated Just For You
            </h1>
            <p className="mt-2 text-sm text-indigo-100 leading-relaxed">
              {user
                ? `Welcome back, ${user.firstName || "Shopper"}. Products ranked by your browsing history, cart additions, and category preferences.`
                : "Sign in to activate real-time personalized recommendations based on your behavior. Showing trending products as preview."}
            </p>
            {!user && (
              <Link
                to="/login"
                className="mt-5 inline-flex items-center gap-2 rounded-xl bg-white px-4 py-2.5 text-sm font-bold text-brand shadow-sm hover:bg-indigo-50"
              >
                Sign in to customize
              </Link>
            )}
          </div>
        </section>
      )}

      {/* Trending Now Rail on Catalog Homepage */}
      {!searchMode && !feedMode && trending.length > 0 && (
        <MiniProductRail
          title="Trending Now"
          description="Most Popular Products"
          badgeIcon={Flame}
          products={trending}
          onCart={addCart}
          onWish={addWish}
        />
      )}

      {/* Catalog Title */}
      <div id="catalog" className="pt-2">
        <PageTitle
          eyebrow={feedMode ? "Picked For You" : searchMode ? "Search" : "The Catalog"}
          title={
            searchMode
              ? `Results for “${keyword}”`
              : feedMode
                ? "Recommended Products"
                : "Explore All Products"
          }
          description={
            searchMode
              ? "Products matching your search query."
              : feedMode
                ? "Recommendations generated by the hybrid scoring engine."
                : "Browse products with real-time stock and automated discount detection."
          }
        />
      </div>

      {/* Category Quick Filter Chips */}
      {!searchMode && !feedMode && (
        <div className="mb-6 flex flex-wrap items-center gap-2">
          {quickCategories.map((c) => {
            const isSelected =
              (c === "All" && !filters.category) || filters.category === c;
            return (
              <button
                key={c}
                onClick={() => {
                  setFilters({ ...filters, category: c === "All" ? "" : c });
                  setParams({ page: "0" });
                  load();
                }}
                className={`rounded-full px-4 py-1.5 text-xs font-semibold transition-all ${
                  isSelected
                    ? "bg-slate-900 text-white shadow-sm"
                    : "border border-slate-200/90 bg-white text-slate-600 hover:border-slate-300 hover:text-slate-900"
                }`}
              >
                {c}
              </button>
            );
          })}
        </div>
      )}

      {notice && (
        <div className="mb-5">
          <Notice success={notice} onDismiss={() => setNotice("")} />
        </div>
      )}

      {/* Advanced Filters Bar */}
      {!searchMode && !feedMode && (
        <form
          onSubmit={(e) => {
            e.preventDefault();
            setParams({ page: "0" });
            load();
          }}
          className="mb-8 grid grid-cols-2 gap-3 rounded-2xl border border-slate-200/80 bg-white p-4 shadow-card sm:grid-cols-3 lg:grid-cols-6"
        >
          <div className="col-span-2 flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-slate-600 lg:col-span-1">
            <SlidersHorizontal size={15} className="text-brand" /> Filter
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
            aria-label="Min price"
            value={filters.minPrice}
            onChange={(e) =>
              setFilters({ ...filters, minPrice: e.target.value })
            }
          />
          <Field
            type="number"
            min="0"
            placeholder="Max price"
            aria-label="Max price"
            value={filters.maxPrice}
            onChange={(e) =>
              setFilters({ ...filters, maxPrice: e.target.value })
            }
          />
          <div className="col-span-2 flex gap-2 lg:col-span-1">
            <Button type="submit" className="w-full">
              Apply
            </Button>
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
          detail="Try adjusting your search keywords or filter criteria."
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

      {/* Pagination */}
      {!loading && !error && !Array.isArray(data) && data?.totalPages > 1 && (
        <div className="mt-10 flex items-center justify-center gap-3">
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
          <span className="text-xs font-semibold text-slate-500">
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
  const [similar, setSimilar] = useState([]);
  const [fbt, setFbt] = useState([]);
  const [selectedImg, setSelectedImg] = useState("");
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
          setSelectedImg(
            p.data.images?.find((i) => i.primaryImage)?.imageUrl ||
              p.data.images?.[0]?.imageUrl ||
              "",
          );

          // Track product view event
          apiRoutes
            .trackEvent({
              userId: user?.userId,
              productId: Number(id),
              eventType: "PRODUCT_VIEW",
            })
            .catch(() => {});
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
  }, [id, user]);

  // Load similar & FBT recommendations independently
  useEffect(() => {
    if (!id || !/^\d+$/.test(id)) return;
    apiRoutes
      .similarProducts(id, 8)
      .then((r) => setSimilar(Array.isArray(r.data) ? r.data : []))
      .catch(() => {});
    apiRoutes
      .fbt(id, 4)
      .then((r) => setFbt(Array.isArray(r.data) ? r.data : []))
      .catch(() => {});
  }, [id]);

  async function addRecommendedToCart(p) {
    if (!user) {
      setNotice("Please sign in first.");
      return;
    }
    try {
      await apiRoutes.addToCart({
        userId: user.userId,
        productId: p.id,
        quantity: 1,
      });
      setNotice(`${p.name} added to cart.`);
      apiRoutes
        .trackEvent({
          userId: user.userId,
          productId: p.id,
          eventType: "ADD_TO_CART",
        })
        .catch(() => {});
    } catch (e) {
      setNotice(e.response?.data?.message || "Could not add to cart.");
    }
  }

  async function addRecommendedToWish(p) {
    if (!user) {
      setNotice("Please sign in first.");
      return;
    }
    try {
      await apiRoutes.addWishlist({ userId: user.userId, productId: p.id });
      setNotice(`${p.name} added to wishlist.`);
      apiRoutes
        .trackEvent({
          userId: user.userId,
          productId: p.id,
          eventType: "ADD_TO_WISHLIST",
        })
        .catch(() => {});
    } catch (e) {
      setNotice(e.response?.data?.message || "Could not add to wishlist.");
    }
  }

  async function add(kind) {
    if (!user) {
      setNotice("Please sign in first.");
      return;
    }
    try {
      if (kind === "cart") {
        await apiRoutes.addToCart({
          userId: user.userId,
          productId: product.id,
          quantity: 1,
        });
        apiRoutes
          .trackEvent({
            userId: user.userId,
            productId: product.id,
            eventType: "ADD_TO_CART",
          })
          .catch(() => {});
      } else {
        await apiRoutes.addWishlist({
          userId: user.userId,
          productId: product.id,
        });
        apiRoutes
          .trackEvent({
            userId: user.userId,
            productId: product.id,
            eventType: "ADD_TO_WISHLIST",
          })
          .catch(() => {});
      }
      setNotice(kind === "cart" ? "Added to cart." : "Added to wishlist.");
    } catch (e) {
      setNotice(e.response?.data?.message || "Request failed.");
    }
  }

  async function buyFbtBundle() {
    if (!user) {
      setNotice("Please sign in first.");
      return;
    }
    if (!fbt[0]) return;
    try {
      await Promise.all([
        apiRoutes.addToCart({
          userId: user.userId,
          productId: product.id,
          quantity: 1,
        }),
        apiRoutes.addToCart({
          userId: user.userId,
          productId: fbt[0].id,
          quantity: 1,
        }),
      ]);
      setNotice("Both items added to your cart!");
    } catch (e) {
      setNotice("Could not add bundle to cart.");
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
      setNotice("Thank you! Review submitted successfully.");
      apiRoutes
        .trackEvent({
          userId: user.userId,
          productId: Number(id),
          eventType: "REVIEW",
        })
        .catch(() => {});
    } catch (e) {
      setNotice(e.response?.data?.message || "Could not submit review.");
    }
  }

  if (loading) return <Loading />;
  if (error) return <Notice error={error} />;
  if (!product) return <Empty title="Product not found" />;

  const discount = calculateDiscount(product.basePrice, product.currentPrice);
  const images = product.images || [];

  return (
    <>
      {/* Breadcrumbs */}
      <nav className="mb-6 flex items-center gap-2 text-xs text-slate-500">
        <Link to="/" className="hover:text-brand transition">
          Home
        </Link>
        <span>/</span>
        <span className="capitalize">{product.category || "Product"}</span>
        <span>/</span>
        <span className="font-semibold text-slate-800 line-clamp-1">
          {product.name}
        </span>
      </nav>

      {/* Main Two-Column Product Layout */}
      <div className="grid gap-10 lg:grid-cols-2">
        {/* Left: Image Gallery */}
        <div className="space-y-4">
          <div className="group relative flex aspect-square w-full items-center justify-center overflow-hidden rounded-3xl border border-slate-200/80 bg-white p-6 shadow-card">
            {selectedImg ? (
              <img
                src={selectedImg}
                alt={product.name}
                className="max-h-full max-w-full object-contain transition-transform duration-500 group-hover:scale-105"
              />
            ) : (
              <span className="text-sm text-slate-400">No product image available</span>
            )}
            {discount && (
              <span className="absolute left-4 top-4 rounded-xl bg-rose-600 px-2.5 py-1 text-xs font-bold text-white shadow-sm">
                {discount}% OFF
              </span>
            )}
          </div>

          {/* Thumbnails */}
          {images.length > 1 && (
            <div className="flex gap-3 overflow-x-auto pb-2">
              {images.map((imgObj, idx) => (
                <button
                  key={idx}
                  onClick={() => setSelectedImg(imgObj.imageUrl)}
                  className={`h-16 w-16 shrink-0 overflow-hidden rounded-xl border-2 bg-white transition ${
                    selectedImg === imgObj.imageUrl
                      ? "border-brand shadow-sm"
                      : "border-slate-200 hover:border-slate-300"
                  }`}
                >
                  <img
                    src={imgObj.imageUrl}
                    alt={imgObj.altText || ""}
                    className="h-full w-full object-contain"
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Right: Info & Purchase Action */}
        <div className="flex flex-col">
          <div className="flex items-center justify-between gap-3">
            <span className="text-xs font-bold uppercase tracking-widest text-brand">
              {product.brand || product.category}
            </span>
            {product.active === false ? (
              <Badge variant="rose">Unavailable</Badge>
            ) : product.stockQuantity > 0 ? (
              <Badge variant="emerald">
                {product.stockQuantity} in stock
              </Badge>
            ) : (
              <Badge variant="rose">Out of stock</Badge>
            )}
          </div>

          <h1 className="mt-2 text-3xl font-extrabold tracking-tight text-ink sm:text-4xl">
            {product.name}
          </h1>

          {/* Reviews & SKU */}
          <div className="mt-3 flex flex-wrap items-center gap-3 text-xs text-slate-500">
            <RatingStars
              rating={product.averageRating}
              totalReviews={product.totalReviews}
              size={15}
            />
            <span>·</span>
            <span>SKU: {product.sku || "—"}</span>
          </div>

          {/* Pricing Display */}
          <div className="mt-6 rounded-2xl border border-slate-200/80 bg-white p-5 shadow-card">
            <div className="flex items-baseline gap-3">
              <span className="text-3xl font-black text-ink">
                {money(product.currentPrice)}
              </span>
              {discount && (
                <>
                  <span className="text-base text-slate-400 line-through">
                    {money(product.basePrice)}
                  </span>
                  <span className="text-xs font-bold text-emerald-600">
                    Save {money(product.basePrice - product.currentPrice)}
                  </span>
                </>
              )}
            </div>

            {/* CTA Buttons */}
            <div className="mt-5 flex flex-wrap gap-3">
              <Button
                size="lg"
                onClick={() => add("cart")}
                disabled={product.active === false || product.stockQuantity === 0}
                className="flex-1 shadow-glow"
              >
                <ShoppingBag size={18} /> Add to Cart
              </Button>
              <Button
                size="lg"
                variant="secondary"
                onClick={() => add("wishlist")}
              >
                <Heart size={18} /> Wishlist
              </Button>
            </div>
          </div>

          {notice && (
            <div className="mt-4">
              <Notice success={notice} onDismiss={() => setNotice("")} />
            </div>
          )}

          {/* Description */}
          <div className="mt-6">
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
              Description
            </h3>
            <p className="mt-2 text-sm leading-relaxed text-slate-600">
              {product.description || "No description provided for this product."}
            </p>
          </div>

          {/* Specifications Table */}
          {product.attributes && Object.keys(product.attributes).length > 0 && (
            <div className="mt-6 rounded-2xl border border-slate-200/80 bg-white p-5 shadow-card">
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500">
                Specifications
              </h3>
              <dl className="mt-3 divide-y divide-slate-100 text-sm">
                {Object.entries(product.attributes).map(([k, v]) => (
                  <div key={k} className="flex justify-between py-2">
                    <dt className="text-slate-500 font-medium">{k}</dt>
                    <dd className="font-semibold text-slate-800 text-right">{v}</dd>
                  </div>
                ))}
              </dl>
            </div>
          )}
        </div>
      </div>

      {/* Frequently Bought Together Bundle */}
      {fbt.length > 0 && (
        <FbtBundleCard
          sourceProduct={product}
          fbtProduct={fbt[0]}
          onBuyBundle={buyFbtBundle}
        />
      )}

      {/* Similar Products Rail */}
      <MiniProductRail
        title="Similar Products"
        description="Content-Based Matches"
        products={similar}
        onCart={addRecommendedToCart}
        onWish={addRecommendedToWish}
      />

      {/* Other Frequently Bought Together rail */}
      {fbt.length > 1 && (
        <MiniProductRail
          title="Frequently Bought Together"
          description="Customers Also Bought"
          badgeIcon={Tag}
          products={fbt.slice(1)}
          onCart={addRecommendedToCart}
          onWish={addRecommendedToWish}
        />
      )}

      {/* Customer Reviews Section */}
      <section className="mt-16">
        <PageTitle
          eyebrow="Feedback"
          title="Customer Reviews"
          description={`Verified reviews and ratings for ${product.name}.`}
        />
        <div className="grid gap-8 lg:grid-cols-2">
          {/* Reviews List */}
          <div className="space-y-4">
            {reviews.length ? (
              reviews.map((r, i) => (
                <article
                  key={r.reviewId || i}
                  className="rounded-2xl border border-slate-200/80 bg-white p-5 shadow-card transition"
                >
                  <div className="flex items-center justify-between gap-3">
                    <div className="flex items-center gap-2.5">
                      <div className="grid h-8 w-8 place-items-center rounded-full bg-brand-50 text-xs font-bold text-brand">
                        {(r.userName || "C")[0].toUpperCase()}
                      </div>
                      <div>
                        <span className="text-sm font-semibold text-slate-800">
                          {r.userName || "Verified Customer"}
                        </span>
                        {r.verifiedPurchase && (
                          <span className="ml-2 rounded bg-emerald-50 px-1.5 py-0.5 text-[10px] font-semibold text-emerald-700">
                            Verified
                          </span>
                        )}
                      </div>
                    </div>
                    <RatingStars rating={r.rating} size={13} />
                  </div>
                  <p className="mt-3 text-sm leading-relaxed text-slate-600">
                    {r.comment || "No written review."}
                  </p>
                  <p className="mt-3 text-xs text-slate-400">
                    {r.createdAt
                      ? new Date(r.createdAt).toLocaleDateString(undefined, {
                          year: "numeric",
                          month: "short",
                          day: "numeric",
                        })
                      : ""}
                  </p>
                </article>
              ))
            ) : (
              <Empty
                title="No reviews yet"
                detail="Be the first to review this product and help others choose."
              />
            )}
          </div>

          {/* Add Review Form */}
          <form
            onSubmit={submitReview}
            className="h-fit rounded-2xl border border-slate-200/80 bg-white p-6 shadow-card"
          >
            <h3 className="text-base font-bold text-ink">Write a Customer Review</h3>
            <p className="mt-1 text-xs text-slate-500">
              Share your thoughts and rate your experience with this item.
            </p>

            <div className="mt-5">
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-2">
                Your Rating
              </label>
              <div className="flex items-center gap-2">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button
                    type="button"
                    key={star}
                    onClick={() => setRating(star)}
                    className="p-1 transition hover:scale-110 active:scale-95"
                  >
                    <Star
                      size={24}
                      className={
                        star <= rating
                          ? "fill-amber-400 text-amber-400"
                          : "text-slate-200"
                      }
                    />
                  </button>
                ))}
                <span className="ml-2 text-sm font-bold text-slate-700">
                  {rating} / 5
                </span>
              </div>
            </div>

            <div className="mt-5">
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-600 mb-1.5">
                Review Comments
              </label>
              <textarea
                rows="4"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                className="w-full rounded-xl border border-slate-200 bg-white p-3 text-sm outline-none transition focus:border-brand focus:ring-4 focus:ring-brand-50"
                placeholder="What did you like or dislike about this product?"
              />
            </div>

            <Button type="submit" className="mt-5 w-full shadow-sm">
              Submit Review
            </Button>
          </form>
        </div>
      </section>
    </>
  );
}
