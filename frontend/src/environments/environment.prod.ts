// Production environment, used by `ng build` (Vercel).
// TODO: Replace these with your deployed backend URLs (API gateway + notification-service WebSocket).
// They must be HTTPS, because Vercel serves the site over HTTPS and browsers block mixed content.
export const environment = {
  apiUrl: 'http://localhost:8080',
  wsUrl: 'http://localhost:8087'
};
