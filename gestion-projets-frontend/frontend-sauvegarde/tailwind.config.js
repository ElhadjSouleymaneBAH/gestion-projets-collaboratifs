/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'collabpro-blue': '#2563eb',
        'collabpro-light': '#dbeafe',
      }
    },
  },
  plugins: [],
}
