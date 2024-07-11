import { env } from 'process';

// /** @type {import('next').NextConfig} */
// global.HTMLImageElement = typeof window === 'undefined' ? Object : window.HTMLImageElement

const nextConfig = {
    output: 'standalone',
    env: {
        // REACT_APP_API_URL_BASE: "http://localhost:8081/api",
        REACT_APP_API_URL_BASE: "http://200.16.7.177/api",
        // REACT_APP_WS_URL_BASE: "ws://localhost:8081/api",
        REACT_APP_WS_URL_BASE: "ws://200.16.7.177/api",
    },
};

export default nextConfig;
