import { AlertCircle, CheckCircle2, Loader2, PackageSearch, Star } from "lucide-react";

export function Button({
  children,
  variant = "primary",
  size = "md",
  loading = false,
  className = "",
  disabled,
  ...props
}) {
  const base =
    "inline-flex items-center justify-center gap-2 font-medium transition-all duration-200 active:scale-[0.98] disabled:cursor-not-allowed disabled:opacity-50 disabled:active:scale-100 select-none";

  const sizeStyles = {
    sm: "px-3 py-1.5 text-xs rounded-lg",
    md: "px-4 py-2.5 text-sm rounded-xl",
    lg: "px-5 py-3 text-base rounded-xl font-semibold",
  }[size] || "px-4 py-2.5 text-sm rounded-xl";

  const variants = {
    primary:
      "bg-brand text-white shadow-sm hover:bg-brand-700 hover:shadow-glow focus-visible:ring-2 focus-visible:ring-brand-500",
    secondary:
      "border border-slate-200/90 bg-white text-slate-700 hover:bg-slate-50 hover:border-slate-300 shadow-sm",
    dark: "bg-ink text-white hover:bg-ink-800 shadow-sm hover:shadow-hover",
    ghost: "text-slate-600 hover:bg-slate-100 hover:text-slate-900",
    danger: "bg-rose-600 text-white hover:bg-rose-700 shadow-sm",
    outline: "border border-brand/30 text-brand bg-brand-50/50 hover:bg-brand-50",
  };

  const style = variants[variant] || variants.primary;

  return (
    <button
      className={`${base} ${sizeStyles} ${style} ${className}`}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <Loader2 className="animate-spin" size={16} />}
      {children}
    </button>
  );
}

export function Field({ label, error, helper, className = "", icon, ...props }) {
  return (
    <label className={`block text-sm font-medium text-slate-700 ${className}`}>
      {label && <span className="mb-1.5 block font-medium text-slate-700">{label}</span>}
      <div className="relative flex items-center">
        {icon && (
          <span className="pointer-events-none absolute left-3.5 text-slate-400">
            {icon}
          </span>
        )}
        <input
          className={`w-full rounded-xl border border-slate-200/90 bg-white px-3.5 py-2.5 text-sm outline-none transition-all duration-150 placeholder:text-slate-400 focus:border-brand focus:ring-4 focus:ring-brand-50 hover:border-slate-300 ${
            icon ? "pl-10" : ""
          } ${error ? "border-rose-300 focus:border-rose-500 focus:ring-rose-50" : ""}`}
          {...props}
        />
      </div>
      {error && <p className="mt-1 text-xs text-rose-600">{error}</p>}
      {helper && !error && <p className="mt-1 text-xs text-slate-500">{helper}</p>}
    </label>
  );
}

export function Badge({ children, variant = "default", className = "" }) {
  const styles = {
    default: "bg-slate-100 text-slate-700 border-slate-200",
    brand: "bg-brand-50 text-brand-700 border-brand-200/60 font-semibold",
    emerald: "bg-emerald-50 text-emerald-700 border-emerald-200/60",
    amber: "bg-amber-50 text-amber-700 border-amber-200/60",
    rose: "bg-rose-50 text-rose-700 border-rose-200/60",
  }[variant] || "bg-slate-100 text-slate-700 border-slate-200";

  return (
    <span
      className={`inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-xs font-medium ${styles} ${className}`}
    >
      {children}
    </span>
  );
}

export function Notice({ error, success, onDismiss }) {
  if (!error && !success) return null;
  const isError = !!error;
  return (
    <div
      role="alert"
      className={`flex items-start justify-between gap-3 rounded-2xl border p-4 text-sm transition-all duration-200 animate-fade-in ${
        isError
          ? "border-rose-200 bg-rose-50/90 text-rose-800"
          : "border-emerald-200 bg-emerald-50/90 text-emerald-800"
      }`}
    >
      <div className="flex items-start gap-2.5">
        {isError ? (
          <AlertCircle size={18} className="mt-0.5 shrink-0 text-rose-600" />
        ) : (
          <CheckCircle2 size={18} className="mt-0.5 shrink-0 text-emerald-600" />
        )}
        <div className="leading-snug">{error || success}</div>
      </div>
      {onDismiss && (
        <button
          onClick={onDismiss}
          className="text-slate-400 hover:text-slate-700 transition"
          aria-label="Dismiss"
        >
          ✕
        </button>
      )}
    </div>
  );
}

export function Loading() {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-slate-500">
      <div className="relative">
        <div className="h-10 w-10 rounded-full border-2 border-brand-100 border-t-brand animate-spin" />
      </div>
      <p className="text-sm font-medium text-slate-500">Loading from PriceIQ…</p>
    </div>
  );
}

export function Empty({
  title = "Nothing here yet",
  detail = "When data is available from your backend, it will appear here.",
  action,
}) {
  return (
    <div className="rounded-3xl border border-dashed border-slate-200 bg-white/60 backdrop-blur-sm px-6 py-16 text-center">
      <div className="mx-auto mb-4 grid h-14 w-14 place-items-center rounded-2xl bg-slate-50 text-slate-400 border border-slate-100">
        <PackageSearch size={26} />
      </div>
      <h3 className="font-semibold text-slate-800 text-base">{title}</h3>
      <p className="mx-auto mt-1 max-w-sm text-sm text-slate-500">{detail}</p>
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}

export function PageTitle({ eyebrow, title, description, action }) {
  return (
    <div className="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
      <div>
        {eyebrow && (
          <p className="mb-2 inline-flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider text-brand">
            <span className="h-1.5 w-1.5 rounded-full bg-brand" />
            {eyebrow}
          </p>
        )}
        <h1 className="text-3xl font-extrabold tracking-tight text-ink sm:text-4xl">
          {title}
        </h1>
        {description && (
          <p className="mt-2 max-w-2xl text-sm leading-relaxed text-slate-500">
            {description}
          </p>
        )}
      </div>
      {action && <div className="shrink-0">{action}</div>}
    </div>
  );
}

export function RatingStars({ rating = 0, totalReviews, size = 15 }) {
  return (
    <div className="flex items-center gap-1 text-amber-500">
      <div className="flex items-center">
        {[1, 2, 3, 4, 5].map((s) => (
          <Star
            key={s}
            size={size}
            className={s <= Math.round(rating) ? "fill-amber-400 text-amber-400" : "text-slate-200"}
          />
        ))}
      </div>
      <span className="ml-1 text-xs font-bold text-slate-700">
        {rating ? Number(rating).toFixed(1) : "—"}
      </span>
      {totalReviews !== undefined && (
        <span className="text-xs text-slate-400">({totalReviews})</span>
      )}
    </div>
  );
}
