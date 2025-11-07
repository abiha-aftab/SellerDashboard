# Seller Dashboard client

Vue 3 + Vite single-page client for the seller metrics API. See the
[project README](../README.md) for the full setup.

```bash
npm ci
npm run dev      # http://localhost:5173, proxies /api to http://localhost:8080
npm test         # Vitest + Vue Test Utils (jsdom)
npm run lint     # ESLint with eslint-plugin-vue
npm run build    # static bundle in dist/
```

Set `VITE_API_PROXY_TARGET` to point the dev proxy at a different backend.
