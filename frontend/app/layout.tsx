import type { Metadata } from "next";
import { Inter, Montserrat } from "next/font/google";
import NextBreadcrumb from '@/components/breadcrumb/NextBreadcrumb'

import "./globals.css";
import Navbar from "@/components/navbar/Navbar";
const inter = Inter({ subsets: ["latin"] });
export const metadata: Metadata = {
    title: "Create Next DP1 RedEx",
    description: "Proyecto del curso DP1",
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en">
            <head>
                <title>RedEx</title>
            </head>

      <body className={`${inter.className} bg-[#EFEFEF] w-auto h-screen`}>
        <Navbar />
        <main className={`h-screen`}>
          <div className="grid grid-cols-[1fr_38fr_1fr]">
            <div></div>
            <div>
              {
                <NextBreadcrumb
                  homeElement={"Inicio"}
                  separator={<span>/</span>}
                  activeClasses="text-gray-700 font-bold text-sm"
                  containerClasses="flex py-5 bg-transparent rounded-t-md text-black"
                  listClasses="px-4 text-sm text-slate-700 hover:text-blue-700"
                  capitalizeLinks
                />
              }
              {children}
            </div>
            <div></div>
          </div>
        </main>
      </body>
    </html>
  );
}
