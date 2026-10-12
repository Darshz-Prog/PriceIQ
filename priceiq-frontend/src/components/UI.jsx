import { AlertCircle, LoaderCircle, PackageSearch } from "lucide-react";
export function Button({
  children,
  variant = "primary",
  className = "",
  ...props
}) {
  const base =
    "inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-50";
  const styles =
    variant === "secondary"
      ? "border border-slate-200 bg-white text-slate-700 hover:bg-slate-50"
      : variant === "ghost"
        ? "text-slate-600 hover:bg-slate-100"
        : variant === "danger"
          ? "bg-rose-600 text-white hover:bg-rose-700"
          : "bg-brand text-white hover:bg-indigo-700";
  return (
    <button className={`${base} ${styles} ${className}`} {...props}>
      {children}
    </button>
  );
}
export function Field({ label, className = "", ...props }) {
  return (
    <label className={`block text-sm font-medium text-slate-700 ${className}`}>
      {label && <span className="mb-1.5 block">{label}</span>}
      <input
        className="w-full rounded-xl border border-slate-200 bg-white px-3.5 py-3 text-sm outline-none transition placeholder:text-slate-400 focus:border-brand focus:ring-4 focus:ring-indigo-100"
        {...props}
      />
    </label>
  );
}
export function Notice({ error }) {
  if (!error) return null;
  return (
    <div
      role="alert"
      className="flex items-start gap-2 rounded-xl border border-rose-200 bg-rose-50 p-3 text-sm text-rose-800"
    >
      <AlertCircle size={18} className="mt-0.5 shrink-0" />
      {error}
    </div>
  );
}
export function Loading() {
  return (
    <div className="flex items-center justify-center gap-2 py-16 text-slate-500">
      <LoaderCircle className="animate-spin" size={20} /> Loading from PriceIQ
      API…
    </div>
  );
}
export function Empty({
  title = "Nothing here yet",
  detail = "When data is available from your backend, it will appear here.",
}) {
  return (
    <div className="rounded-2xl border border-dashed border-slate-200 bg-white px-6 py-14 text-center">
      <PackageSearch className="mx-auto mb-3 text-slate-300" size={34} />
      <h3 className="font-semibold text-slate-800">{title}</h3>
      <p className="mt-1 text-sm text-slate-500">{detail}</p>
    </div>
  );
}
export function PageTitle({ eyebrow, title, description, action }) {
  return (
    <div className="mb-7 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
      <div>
        {eyebrow && (
          <p className="mb-2 text-xs font-bold uppercase tracking-[.18em] text-brand">
            {eyebrow}
          </p>
        )}
        <h1 className="text-3xl font-bold tracking-tight text-ink sm:text-4xl">
          {title}
        </h1>
        {description && (
          <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-500">
            {description}
          </p>
        )}
      </div>
      {action}
    </div>
  );
}
