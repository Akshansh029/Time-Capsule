import Link from "next/link";
import { Button } from "@/components/ui/button";

export function Navbar() {
  return (
    <nav className="fixed top-0 left-0 w-full z-50 transition-all duration-300">
      <div className="container mx-auto px-6 py-4 flex items-center justify-between">
        <Link
          href="/"
          className="font-serif text-2xl font-bold text-primary tracking-wider"
        >
          THE ETERNAL VAULT
        </Link>
        <div className="hidden md:flex items-center space-x-8">
          <Link
            href="/philosophy"
            className="text-sm font-medium hover:text-primary transition-colors uppercase tracking-widest text-muted-foreground"
          >
            Philosophy
          </Link>
          <Link
            href="/archives"
            className="text-sm font-medium hover:text-primary transition-colors uppercase tracking-widest text-muted-foreground"
          >
            Archives
          </Link>
          <Link href="/auth/login">
            <Button
              variant="ghost"
              className="uppercase tracking-widest text-xs border border-primary/20 hover:bg-primary/10"
            >
              Sign In
            </Button>
          </Link>
          <Link href="/auth/register">
            <Button className="gold-gradient text-primary-foreground hover:opacity-90 uppercase tracking-widest text-xs font-bold px-6">
              Invoke Chronos
            </Button>
          </Link>
        </div>
      </div>
    </nav>
  );
}
