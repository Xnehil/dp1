/** @type {import('next').NextConfig} */
global.HTMLImageElement = typeof window === 'undefined' ? Object : window.HTMLImageElement

const nextConfig = {
    output: 'standalone',
};

export default nextConfig;
