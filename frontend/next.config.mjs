import { env } from 'process';

// /** @type {import('next').NextConfig} */
// global.HTMLImageElement = typeof window === 'undefined' ? Object : window.HTMLImageElement

const nextConfig = {
    output: 'standalone',
    env: {
        REACT_APP_API_URL_BASE: "http://localhost:8081",
        // REACT_APP_API_URL_BASE: "http://inf226-982-2b.inf.pucp.edu.pe:8081",
        REACT_APP_WS_URL_BASE: "ws://localhost:8081",
        // REACT_APP_WS_URL_BASE: "ws://inf226-982-2b.inf.pucp.edu.pe:8081",
    },
};

export default nextConfig;
