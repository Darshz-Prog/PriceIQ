import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  Search,
  ShoppingBag,
  Heart,
  UserRound,
  LogOut,
  ChartNoAxesCombined,
  Sparkles,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useState } from "react";
function Header() {
  const { user, signOut } = useAuth();
  const [keyword, setKeyword] = useState("");
  const navigate = useNavigate();
  const submit = (e) => {
    e.preventDefault();
    if (keyword.trim())
      navigate(`/search?keyword=${encodeURIComponent(keyword.trim())}`);
  };
  return (
    <header className="sticky top-0 z-30 border-b border-slate-200/70 bg-white/90 backdrop-blur-xl">
      <div className="mx-auto flex max-w-7xl items-center gap-4 px-4 py-3 sm:px-6 lg:px-8">
        <Link to="/" className="flex shrink-0 items-center gap-2">
          <span className="grid h-9 w-9 place-items-center rounded-xl bg-brand text-white">
            <Sparkles size={19} />
          </span>
          <span className="text-xl font-black tracking-tight text-ink">
            Price<span className="text-brand">IQ</span>
          </span>
        </Link>
        <form
          onSubmit={submit}
          className="ml-auto hidden max-w-xl flex-1 md:flex"
        >
          <div className="flex w-full items-center rounded-xl border border-slate-200 bg-slate-50 px-3">
            <Search size={17} className="text-slate-400" />
            <input
              aria-label="Search products"
              className="w-full bg-transparent px-3 py-2.5 text-sm outline-none"
              placeholder="Search products, brands…"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
            />
            <button className="text-xs font-semibold text-brand">Search</button>
          </div>
        </form>
        <nav className="ml-auto flex items-center gap-1 md:ml-2">
          <NavLink title="Wishlist" to="/wishlist" className="nav-icon">
            <Heart size={19} />
          </NavLink>
          <NavLink title="Cart" to="/cart" className="nav-icon">
            <ShoppingBag size={19} />
          </NavLink>
          {user ? (
            <>
              <NavLink title="Account" to="/account" className="nav-icon">
                <UserRound size={19} />
              </NavLink>
              {user.role === "ADMIN" && (
                <NavLink title="Analytics" to="/admin" className="nav-icon">
                  <ChartNoAxesCombined size={19} />
                </NavLink>
              )}
              <button
                title="Sign out"
                className="nav-icon"
                onClick={() => {
                  signOut();
                  navigate("/login");
                }}
              >
                <LogOut size={18} />
              </button>
            </>
          ) : (
            <Link
              to="/login"
              className="rounded-xl bg-ink px-4 py-2.5 text-sm font-semibold text-white hover:bg-slate-700"
            >
              Sign in
            </Link>
          )}
        </nav>
      </div>
      <form onSubmit={submit} className="px-4 pb-3 md:hidden">
        <div className="flex items-center rounded-xl border border-slate-200 bg-slate-50 px-3">
          <Search size={17} className="text-slate-400" />
          <input
            className="w-full bg-transparent px-3 py-2 text-sm outline-none"
            placeholder="Search products…"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button className="text-xs font-semibold text-brand">Go</button>
        </div>
      </form>
    </header>
  );
}
export default function Layout() {
  return (
    <div className="min-h-screen">
      <Header />
      <main className="mx-auto min-h-[72vh] max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <Outlet />
      </main>
      <footer className="mt-12 border-t border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-2 px-4 py-7 text-sm text-slate-500 sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
          <span className="font-semibold text-slate-700">
            PriceIQ{" "}
            <span className="font-normal text-slate-400">
              · Shop with a little more insight.
            </span>
          </span>
          <span>
            Connected to your Spring Boot API · localhost:8081 by default
          </span>
        </div>
      </footer>
    </div>
  );
}
