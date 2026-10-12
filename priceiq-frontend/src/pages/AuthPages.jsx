import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Sparkles } from "lucide-react";
import { apiRoutes } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { Button, Field, Notice } from "../components/UI";
function AuthShell({ title, subtitle, children, footer }) {
  return (
    <div className="mx-auto grid max-w-4xl overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-soft md:grid-cols-2">
      <div className="hidden flex-col justify-between bg-ink p-8 text-white md:flex">
        <div className="flex items-center gap-2">
          <span className="grid h-9 w-9 place-items-center rounded-xl bg-brand">
            <Sparkles size={19} />
          </span>
          <b className="text-xl">PriceIQ</b>
        </div>
        <div>
          <p className="text-xs font-bold uppercase tracking-[.2em] text-indigo-300">
            Shop with confidence
          </p>
          <h2 className="mt-4 text-3xl font-black leading-tight">
            A better way to discover what matters.
          </h2>
          <p className="mt-4 text-sm leading-6 text-slate-300">
            Sign in to access your cart, wishlist, orders, and personalized
            recommendations.
          </p>
        </div>
        <p className="text-xs text-slate-400">
          Powered by your PriceIQ backend
        </p>
      </div>
      <div className="p-6 sm:p-9">
        <h1 className="text-2xl font-bold">{title}</h1>
        <p className="mt-2 text-sm text-slate-500">{subtitle}</p>
        {children}
        {footer && (
          <p className="mt-6 text-center text-sm text-slate-500">{footer}</p>
        )}
      </div>
    </div>
  );
}
export function LoginPage() {
  const { signIn } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const { data } = await apiRoutes.login(form);
      signIn(data);
      navigate("/");
    } catch (e) {
      setError(
        e.response?.data?.message ||
          "Login failed. Verify your credentials and backend availability.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <AuthShell
      title="Welcome back"
      subtitle="Sign in to continue to PriceIQ."
      footer={
        <>
          New to PriceIQ?{" "}
          <Link to="/register" className="font-semibold text-brand">
            Create an account
          </Link>
        </>
      }
    >
      <form onSubmit={submit} className="mt-6 space-y-4">
        {error && <Notice error={error} />}
        <Field
          label="Email address"
          type="email"
          autoComplete="email"
          required
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          placeholder="you@example.com"
        />
        <Field
          label="Password"
          type="password"
          autoComplete="current-password"
          required
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          placeholder="Your password"
        />
        <Button type="submit" block disabled={busy}>
          {busy ? "Signing in…" : "Sign in"}
        </Button>
      </form>
    </AuthShell>
  );
}
export function RegisterPage() {
  const { signIn } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    phoneNumber: "",
  });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  async function submit(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const { data } = await apiRoutes.register(form);
      if (data.accessToken) signIn(data);
      else navigate("/login");
    } catch (e) {
      setError(
        e.response?.data?.message ||
          "Registration failed. Check the supplied fields and backend response.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <AuthShell
      title="Create your account"
      subtitle="A few details and you're ready to go."
      footer={
        <>
          Already registered?{" "}
          <Link to="/login" className="font-semibold text-brand">
            Sign in
          </Link>
        </>
      }
    >
      <form onSubmit={submit} className="mt-6 space-y-3">
        {error && <Notice error={error} />}
        <div className="grid grid-cols-2 gap-3">
          <Field
            label="First name"
            required
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
          />
          <Field
            label="Last name"
            required
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
          />
        </div>
        <Field
          label="Email address"
          type="email"
          required
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />
        <Field
          label="Password"
          type="password"
          required
          minLength={6}
          maxLength={100}
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />
        <Field
          label="Phone number (optional)"
          type="tel"
          value={form.phoneNumber}
          onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
        />
        <Button type="submit" block disabled={busy}>
          {busy ? "Creating account…" : "Create account"}
        </Button>
      </form>
    </AuthShell>
  );
}
