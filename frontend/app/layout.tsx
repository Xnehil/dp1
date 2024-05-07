import type { Metadata } from "next";
import { Inter, Montserrat } from "next/font/google";
import "./globals.css";
import Navbar from "@/components/navbar/Navbar";
const inter = Inter({ subsets: ["latin"] });
const montserrat = Montserrat({ subsets: ["latin"] });

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
                <div className="fixed top-0 left-0 w-full z-50">
                    <Navbar />
                </div>
                <main className={`h-screen`}>
                    {/* <div className="grid grid-cols-[1fr_10fr_1fr]"> Me estaba dando problemas*/}
                    <div className="m-8 pt-16">
                        <div></div>
                        <div>{children}</div>
                        <div></div>
                    </div>
                </main>
            </body>
        </html>
    );
}
