/**
 * utils.js — Global DOM helpers, UI effects, and error formatting
 */

export const $ = (id) => document.getElementById(id);

export function showEl(...ids) { ids.forEach((id) => $(id)?.classList.remove("hidden")); }
export function hideEl(...ids) { ids.forEach((id) => $(id)?.classList.add("hidden")); }

/**
 * Friendly Firebase error mapper
 */
export function friendlyError(code) {
  const map = {
    "auth/user-not-found":      "No account found with that email.",
    "auth/wrong-password":      "Incorrect password. Try again.",
    "auth/email-already-in-use":"That email is already registered.",
    "auth/invalid-email":       "Please enter a valid email address.",
    "auth/weak-password":       "Password must be at least 6 characters.",
    "auth/too-many-requests":   "Too many attempts. Please try again later.",
    "auth/network-request-failed": "Network error. Check your connection.",
  };
  return map[code] || `Error: ${code}`;
}

/**
 * Initialize ripple effects on elements with .ripple class
 */
export function initRipples() {
  document.querySelectorAll(".ripple").forEach(btn => {
    if (btn.dataset.rippleInit) return;
    btn.dataset.rippleInit = "true";

    btn.addEventListener("click", function(e) {
      const rect = this.getBoundingClientRect();
      const radius = Math.max(rect.width, rect.height);
      const circle = document.createElement("span");
      
      const diameter = radius * 2;
      circle.style.width = circle.style.height = `${diameter}px`;
      circle.style.left = `${e.clientX - rect.left - radius}px`;
      circle.style.top = `${e.clientY - rect.top - radius}px`;
      circle.classList.add("ripple-pulse");
      
      this.appendChild(circle);
      setTimeout(() => circle.remove(), 600);
    });
  });
}
