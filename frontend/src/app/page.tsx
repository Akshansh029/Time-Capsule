import { Navbar } from "@/components/Navbar";
import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function LandingPage() {
  return (
    <div className="flex flex-col min-h-screen">
      <Navbar />

      <main className="flex-grow">
        {/* Hero Section */}
        <section className="relative pt-32 pb-20 md:pt-48 md:pb-32 overflow-hidden">
          <div className="container mx-auto px-6 relative z-10">
            <div className="max-w-4xl">
              <h1 className="font-serif text-5xl md:text-7xl lg:text-8xl text-foreground leading-[1.1] tracking-tight mb-8">
                Preserve your <br />
                <span className="text-primary italic">memories</span> for <br />
                the future.
              </h1>
              <p className="text-muted-foreground text-lg md:text-xl max-w-2xl mb-12 leading-relaxed">
                A digital sanctum designed for permanence. Secure your thoughts,
                media, and milestones in a vault that transcends generations.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Link href="/auth/register">
                  <Button className="gold-gradient text-primary-foreground hover:opacity-90 px-10 py-7 text-lg rounded-md font-bold uppercase tracking-widest transition-all hover:scale-105 duration-300 shadow-2xl shadow-primary/20">
                    Get Started
                  </Button>
                </Link>
                <Link href="/philosophy">
                  <Button
                    variant="outline"
                    className="px-10 py-7 text-lg rounded-md border-primary/20 hover:bg-primary/5 uppercase tracking-widest text-muted-foreground"
                  >
                    Our Philosophy
                  </Button>
                </Link>
              </div>
            </div>
          </div>

          {/* Decorative background elements */}
          <div className="absolute top-1/2 -right-64 w-[600px] h-[600px] bg-primary/10 rounded-full blur-[120px] -z-10" />
          <div className="absolute -bottom-32 -left-32 w-96 h-96 bg-primary/5 rounded-full blur-[100px] -z-10" />
        </section>

        {/* Process Section */}
        <section className="py-24 bg-[#1a1a1a]/40 relative">
          <div className="container mx-auto px-6">
            <div className="grid md:grid-cols-3 gap-12">
              <div className="glass p-10 rounded-2xl group hover:bg-white/10 transition-all duration-500">
                <div className="text-primary font-serif text-4xl mb-6 opacity-50 group-hover:opacity-100 transition-opacity">
                  01
                </div>
                <h3 className="font-serif text-2xl text-foreground mb-4">
                  Create
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  Compose messages, upload high-fidelity media, or record voice
                  memos. Every entry is encrypted the moment it's conceived.
                </p>
              </div>

              <div className="glass p-10 rounded-2xl group hover:bg-white/10 transition-all duration-500 md:mt-12">
                <div className="text-primary font-serif text-4xl mb-6 opacity-50 group-hover:opacity-100 transition-opacity">
                  02
                </div>
                <h3 className="font-serif text-2xl text-foreground mb-4">
                  Seal
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  Define the duration of the seal. Whether it's five years or
                  fifty, your vault remains impenetrable until the chosen hour.
                </p>
              </div>

              <div className="glass p-10 rounded-2xl group hover:bg-white/10 transition-all duration-500 md:mt-24">
                <div className="text-primary font-serif text-4xl mb-6 opacity-50 group-hover:opacity-100 transition-opacity">
                  03
                </div>
                <h3 className="font-serif text-2xl text-foreground mb-4">
                  Reveal
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  At the designated time, your chosen recipients receive the
                  key. A bridge through time, delivered with absolute certainty.
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* Teaser Section */}
        <section className="py-32">
          <div className="container mx-auto px-6 text-center max-w-4xl">
            <h2 className="font-serif text-4xl md:text-5xl text-foreground mb-8">
              History is in your hands.
            </h2>
            <p className="text-muted-foreground text-lg mb-12 leading-relaxed">
              Join the elite few who are eternalizing their digital existence.
              Reserved memberships available for those who value their legacy.
            </p>
            <Link href="/auth/register">
              <Button
                variant="link"
                className="text-primary uppercase tracking-[0.3em] font-bold text-sm underline-offset-8"
              >
                Begin your preservation journey
              </Button>
            </Link>
          </div>
        </section>
      </main>

      <footer className="py-12 border-t border-white/5">
        <div className="container mx-auto px-6 flex flex-col md:flex-row justify-between items-center text-muted-foreground text-xs uppercase tracking-widest gap-6">
          <div>© 2024 The Eternal Vault. Preserving moments for eternity.</div>
          <div className="flex gap-8">
            <Link
              href="/privacy"
              className="hover:text-primary transition-colors"
            >
              Privacy of Memory
            </Link>
            <Link
              href="/terms"
              className="hover:text-primary transition-colors"
            >
              Terms of Service
            </Link>
          </div>
        </div>
      </footer>
    </div>
  );
}
