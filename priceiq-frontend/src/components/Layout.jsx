import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  Search,
  ShoppingBag,
  Heart,
  UserRound,
  LogOut,
  ChartNoAxesCombined,
  Sparkles,
  Compass,
  Menu,
  X,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useState } from "react";

function Header() {
  const { user, signOut } = useAuth();
  const [keyword, setKeyword] = useState("");
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const navigate = useNavigate();

  const submit = (e) => {
    e.preventDefault();
    if (keyword.trim()) {
      navigate(`/search?keyword=${encodeURIComponent(keyword.trim())}`);
      setMobileMenuOpen(false);
    }
  };

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200/80 bg-white/85 backdrop-blur-xl transition-all">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3 sm:px-6 lg:px-8">
        {/* Brand Logo */}
        <div className="flex items-center gap-6">
          <Link to="/" className="group flex items-center gap-2.5">
            <span className="grid h-10 w-10 place-items-center rounded-2xl bg-gradient-to-tr from-brand to-brand-500 text-white shadow-sm transition-transform duration-300 group-hover:scale-105">
              <Sparkles size={20} className="transition-transform group-hover:rotate-12" />
            </span>
            <div className="flex flex-col">
              <span className="text-xl font-extrabold tracking-tight text-ink">
                Price<span className="text-brand">IQ</span>
              </span>
              <span className="hidden text-[10px] font-semibold uppercase tracking-wider text-slate-400 sm:inline-block">
                Smart Commerce
              </span>
            </div>
          </Link>

          {/* Primary Nav Links */}
          <nav className="hidden items-center gap-1 md:flex">
            <NavLink
              to="/"
              end
              className={({ isActive }) =>
                `flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-sm font-medium transition ${
                  isActive
                    ? "bg-slate-100/90 text-brand font-semibold"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`
              }
            >
              <Compass size={16} /> Explore
            </NavLink>
            <NavLink
              to="/recommendations"
              className={({ isActive }) =>
                `flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-sm font-medium transition ${
                  isActive
                    ? "bg-brand-50 text-brand font-semibold"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`
              }
            >
              <Sparkles size={16} className="text-brand" />
              <span>For You</span>
              <span className="rounded-full bg-brand/10 px-1.5 py-0.2 text-[10px] font-bold text-brand uppercase">
                AI
              </span>
            </NavLink>
          </nav>
        </div>

        {/* Global Search Bar */}
        <form onSubmit={submit} className="hidden max-w-md flex-1 lg:flex">
          <div className="relative flex w-full items-center">
            <Search size={16} className="pointer-events-none absolute left-3.5 text-slate-400" />
            <input
              aria-label="Search products"
              className="w-full rounded-2xl border border-slate-200/90 bg-slate-50/70 py-2 pl-10 pr-20 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-brand focus:bg-white focus:ring-4 focus:ring-brand-50 hover:border-slate-300"
              placeholder="Search products, categories, brands…"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <button
              type="submit"
              className="absolute right-1.5 rounded-xl bg-white px-3 py-1 text-xs font-semibold text-brand shadow-sm border border-slate-200/60 hover:bg-brand-50 transition"
            >
              Search
            </button>
          </div>
        </form>

        {/* User Navigation Icons */}
        <div className="flex items-center gap-1.5">
          <NavLink
            title="Wishlist"
            to="/wishlist"
            className={({ isActive }) =>
              `nav-icon ${isActive ? "active" : ""}`
            }
          >
            <Heart size={19} />
          </NavLink>
          <NavLink
            title="Cart"
            to="/cart"
            className={({ isActive }) =>
              `nav-icon ${isActive ? "active" : ""}`
            }
          >
            <ShoppingBag size={19} />
          </NavLink>

          {user ? (
            <div className="flex items-center gap-1">
              <NavLink
                title="Account"
                to="/account"
                className={({ isActive }) =>
                  `nav-icon ${isActive ? "active" : ""}`
                }
              >
                <UserRound size={19} />
              </NavLink>
              {user.role === "ADMIN" && (
                <NavLink
                  title="Admin Dashboard"
                  to="/admin"
                  className={({ isActive }) =>
                    `nav-icon ${isActive ? "active" : ""}`
                  }
                >
                  <ChartNoAxesCombined size={19} className="text-brand" />
                </NavLink>
              )}
              <button
                title="Sign out"
                className="nav-icon hover:text-rose-600"
                onClick={() => {
                  signOut();
                  navigate("/login");
                }}
              >
                <LogOut size={18} />
              </button>
            </div>
          ) : (
            <Link
              to="/login"
              className="ml-2 inline-flex items-center rounded-xl bg-ink px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-ink-800 hover:shadow-soft active:scale-[0.98]"
            >
              Sign in
            </Link>
          )}

          {/* Mobile menu button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="rounded-xl p-2 text-slate-600 hover:bg-slate-100 md:hidden ml-1"
            aria-label="Toggle menu"
          >
            {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="border-t border-slate-200/80 bg-white/95 px-4 py-3 md:hidden space-y-3 animate-fade-in backdrop-blur-xl">
          <form onSubmit={submit} className="flex">
            <div className="relative flex w-full items-center">
              <Search size={16} className="pointer-events-none absolute left-3.5 text-slate-400" />
              <input
                className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2 pl-10 pr-16 text-sm outline-none"
                placeholder="Search products…"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
              />
              <button
                type="submit"
                className="absolute right-1.5 rounded-lg bg-brand px-2.5 py-1 text-xs font-semibold text-white"
              >
                Go
              </button>
            </div>
          </form>
          <div className="flex flex-col gap-1 pt-1">
            <Link
              to="/"
              onClick={() => setMobileMenuOpen(false)}
              className="flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100"
            >
              <Compass size={17} /> Explore Catalog
            </Link>
            <Link
              to="/recommendations"
              onClick={() => setMobileMenuOpen(false)}
              className="flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-medium text-brand hover:bg-brand-50"
            >
              <Sparkles size={17} /> Recommended For You
            </Link>
            {user?.role === "ADMIN" && (
              <Link
                to="/admin"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100"
              >
                <ChartNoAxesCombined size={17} /> Admin Analytics
              </Link>
            )}
          </div>
        </div>
      )}
    </header>
  );
}

export default function Layout() {
  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <Header />
      <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-8 sm:px-6 lg:px-8">
        <Outlet />
      </main>
      <footer className="mt-16 border-t border-slate-200/80 bg-white">
        <div className="mx-auto flex max-w-7xl flex-col justify-between gap-6 px-4 py-8 sm:flex-row sm:items-center sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <span className="grid h-8 w-8 place-items-center rounded-xl bg-brand text-white">
              <Sparkles size={16} />
            </span>
            <div>
              <p className="text-sm font-bold text-slate-800">
                Price<span className="text-brand">IQ</span>
              </p>
              <p className="text-xs text-slate-400">
                Hybrid Recommendation E-Commerce Engine
              </p>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-6 text-xs text-slate-500">
            <Link to="/" className="hover:text-brand transition">
              Explore Catalog
            </Link>
            <Link to="/recommendations" className="hover:text-brand transition">
              AI Feed
            </Link>
            <Link to="/cart" className="hover:text-brand transition">
              Cart
            </Link>
            <Link to="/wishlist" className="hover:text-brand transition">
              Wishlist
            </Link>
            <div className="flex items-center gap-1.5 text-emerald-600 font-medium">
              <span className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
              API Connected
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
