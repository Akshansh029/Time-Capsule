import type { Metadata } from "next";
import { Inter, Noto_Serif } from "next/font/google";
import "./globals.css";
import { cn } from "@/lib/utils";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
});

const notoSerif = Noto_Serif({
  subsets: ["latin"],
  variable: "--font-noto-serif",
  weight: ["400", "700"],
});

export const metadata: Metadata = {
  title: "The Eternal Vault | Time Capsule",
  description:
    "Preserve your memories for the future in a secure digital sanctum.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body
        className={cn(
          "min-h-screen bg-background font-sans antialiased",
          inter.variable,
          notoSerif.variable,
        )}
      >
        {children}
      </body>
    </html>
  );
}
