import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowRight, Lock, Mail, Phone, ShieldCheck, Sparkles, User } from "lucide-react";
import { apiRoutes } from "../api/client";
import { useAuth } from "../context/AuthContext";
import { Button, Field, Notice } from "../components/UI";

function AuthShell({ title, subtitle, children, footer }) {
  return (
    <div className="mx-auto grid max-w-4xl overflow-hidden rounded-3xl border border-slate-200/80 bg-white shadow-card md:grid-cols-2">
      {/* Brand Side Panel */}
      <div className="relative hidden flex-col justify-between overflow-hidden bg-ink p-10 text-white md:flex">
        <div className="pointer-events-none absolute -right-16 -top-16 h-64 w-64 rounded-full bg-brand/30 blur-3xl" />
        <div className="pointer-events-none absolute -left-16 -bottom-16 h-64 w-64 rounded-full bg-accent-violet/20 blur-3xl" />

        <div className="relative z-10 flex items-center gap-2.5">
          <span className="grid h-10 w-10 place-items-center rounded-2xl bg-gradient-to-tr from-brand to-brand-500 text-white shadow-sm">
            <Sparkles size={20} />
          </span>
          <span className="text-xl font-extrabold tracking-tight">
            Price<span className="text-brand-500">IQ</span>
          </span>
        </div>

        <div className="relative z-10 my-auto py-10">
          <span className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/10 px-3 py-1 text-xs font-semibold text-indigo-200 backdrop-blur-md">
            <ShieldCheck size={14} className="text-emerald-400" /> Secure Shopping
          </span>
          <h2 className="mt-5 text-3xl font-extrabold tracking-tight text-white leading-tight">
            Intelligent pricing and instant recommendations.
          </h2>
          <p className="mt-3 text-sm leading-relaxed text-slate-300">
            Sign in to unlock personalized hybrid recommendations, save your wishlist, and track orders in real time.
          </p>
        </div>

        <div className="relative z-10 text-xs text-slate-400">
          Powered by PriceIQ Spring Boot & Kafka Engine
        </div>
      </div>

      {/* Auth Form Container */}
      <div className="p-8 sm:p-10 flex flex-col justify-center">
        <div>
          <h1 className="text-2xl font-black tracking-tight text-ink">{title}</h1>
          <p className="mt-1 text-xs text-slate-500">{subtitle}</p>
        </div>

        <div className="mt-6">{children}</div>

        {footer && (
          <p className="mt-6 text-center text-xs text-slate-500">{footer}</p>
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

      // Track login event
      if (data.userId) {
        apiRoutes
          .trackEvent({ userId: data.userId, eventType: "LOGIN" })
          .catch(() => {});
      }

      navigate("/");
    } catch (e) {
      setError(
        e.response?.data?.message ||
          "Login failed. Please verify your email and password.",
      );
    } finally {
      setBusy(false);
    }
  }

  return (
    <AuthShell
      title="Welcome back"
      subtitle="Sign in to your PriceIQ account."
      footer={
        <>
          Don't have an account yet?{" "}
          <Link to="/register" className="font-bold text-brand hover:underline">
            Create account
          </Link>
        </>
      }
    >
      <form onSubmit={submit} className="space-y-4">
        {error && <Notice error={error} />}

        <Field
          label="Email address"
          type="email"
          autoComplete="email"
          required
          icon={<Mail size={16} />}
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          placeholder="you@example.com"
        />

        <Field
          label="Password"
          type="password"
          autoComplete="current-password"
          required
          icon={<Lock size={16} />}
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          placeholder="••••••••"
        />

        <div className="pt-2">
          <Button
            type="submit"
            size="lg"
            disabled={busy}
            loading={busy}
            className="w-full shadow-glow"
          >
            Sign in <ArrowRight size={16} />
          </Button>
        </div>
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
      if (data.accessToken) {
        signIn(data);
        navigate("/");
      } else {
        navigate("/login");
      }
    } catch (e) {
      setError(
        e.response?.data?.message ||
          "Registration failed. Please check the entered fields.",
      );
    } finally {
      setBusy(false);
    }
  }

  return (
    <AuthShell
      title="Create account"
      subtitle="Join PriceIQ for personalized smart recommendations."
      footer={
        <>
          Already registered?{" "}
          <Link to="/login" className="font-bold text-brand hover:underline">
            Sign in
          </Link>
        </>
      }
    >
      <form onSubmit={submit} className="space-y-3.5">
        {error && <Notice error={error} />}

        <div className="grid grid-cols-2 gap-3">
          <Field
            label="First name"
            required
            value={form.firstName}
            onChange={(e) => setForm({ ...form, firstName: e.target.value })}
            placeholder="John"
          />
          <Field
            label="Last name"
            required
            value={form.lastName}
            onChange={(e) => setForm({ ...form, lastName: e.target.value })}
            placeholder="Doe"
          />
        </div>

        <Field
          label="Email address"
          type="email"
          required
          icon={<Mail size={16} />}
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          placeholder="john@example.com"
        />

        <Field
          label="Password"
          type="password"
          required
          minLength={6}
          icon={<Lock size={16} />}
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          placeholder="At least 6 characters"
        />

        <Field
          label="Phone number (optional)"
          type="tel"
          icon={<Phone size={16} />}
          value={form.phoneNumber}
          onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
          placeholder="+91 98765 43210"
        />

        <div className="pt-2">
          <Button
            type="submit"
            size="lg"
            disabled={busy}
            loading={busy}
            className="w-full shadow-glow"
          >
            Create Account <ArrowRight size={16} />
          </Button>
        </div>
      </form>
    </AuthShell>
  );
}
