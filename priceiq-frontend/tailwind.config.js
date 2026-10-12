/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: { ink: "#17202a", brand: "#6558f5", mint: "#d8f7e7" },
      boxShadow: { soft: "0 12px 40px rgba(24,32,50,.08)" },
    },
  },
  plugins: [],
};
