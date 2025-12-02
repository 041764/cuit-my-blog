const ABSOLUTE_URL_REGEXP = /^https?:\/\//i

function normalizeOrigin(origin) {
  return origin ? origin.replace(/\/$/, '') : ''
}

function deriveBackendOrigin() {
  // Prefer explicit configuration, fall back to sensible defaults.
  const configured = normalizeOrigin(import.meta.env.VITE_BACKEND_ORIGIN)
  if (configured) {
    return configured
  }

  if (typeof window === 'undefined') {
    return ''
  }

  const { protocol, hostname, port } = window.location
  const isLocalhost = hostname === 'localhost' || hostname === '127.0.0.1'

  if (isLocalhost && port && port !== '8080') {
    return `${protocol}//${hostname}:8080`
  }

  // When running on the same origin as backend we can just reuse it.
  const derivedPort = port ? `:${port}` : ''
  return `${protocol}//${hostname}${derivedPort}`
}

const backendOrigin = deriveBackendOrigin()

export function resolveAssetUrl(url) {
  if (!url) {
    return ''
  }

  if (ABSOLUTE_URL_REGEXP.test(url)) {
    return url
  }

  const normalizedPath = url.startsWith('/') ? url : `/${url}`
  if (!backendOrigin) {
    return normalizedPath
  }
  return `${backendOrigin}${normalizedPath}`
}
