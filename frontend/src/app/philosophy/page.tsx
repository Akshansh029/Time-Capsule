"use client";
import React from "react";
import { Navbar } from "@/components/Navbar";
import { LockKeyhole, Hourglass, ShieldCheck } from "lucide-react";

export default function PhilosophyPage() {
  return (
    <div className="min-h-screen bg-[#131313] text-foreground font-sans selection:bg-primary/30 flex flex-col relative overflow-hidden">
      <Navbar />

      <main className="flex-grow flex items-center justify-center container mx-auto px-6 py-10 mt-16 max-w-5xl relative z-10">
        <div className="glass-variant p-8 md:p-16 rounded-[3.5rem] border-primary/20 relative overflow-hidden group w-full text-center shadow-2xl">
          <div className="absolute top-0 right-0 w-64 h-64 bg-primary/5 blur-[100px] -mr-32 -mt-32 rounded-full" />
          <div className="absolute bottom-0 left-0 w-64 h-64 bg-primary/5 blur-[100px] -ml-32 -mb-32 rounded-full" />

          <div className="relative space-y-8 max-w-3xl mx-auto">
            <h1 className="font-serif text-4xl md:text-5xl lg:text-6xl text-foreground tracking-tight">
              Our <span className="text-primary italic">Philosophy</span>
            </h1>

            <p className="text-muted-foreground uppercase tracking-[0.25em] text-[10px] md:text-xs leading-loose">
              Time is fleeting. Memories are eternal.
            </p>

            <div className="w-16 h-px bg-gradient-to-r from-transparent via-primary/50 to-transparent mx-auto my-8" />

            <div className="prose prose-invert max-w-none text-center mt-12 mb-12">
              <p className="text-sm md:text-base leading-relaxed text-foreground/80 font-light mx-auto max-w-2xl italic">
                "We believe that human experience is the most valuable artifact
                of our existence. In an era of disposable data and fleeting
                moments, the Eternal Vault stands as a testament to permanence.
                Our mission is to provide an inviolable sanctuary where the
                present can speak directly to the future."
              </p>
            </div>

            <div className="flex flex-col md:flex-row justify-center items-center gap-8 md:gap-16 pt-12 border-t border-white/5">
              <div className="flex flex-col items-center space-y-3">
                <div className="w-12 h-12 rounded-full bg-primary/5 border border-primary/20 flex items-center justify-center text-primary group-hover:scale-110 transition-transform duration-500">
                  <Hourglass className="w-5 h-5" />
                </div>
                <span className="text-[10px] uppercase tracking-widest font-bold text-muted-foreground">
                  Patience
                </span>
              </div>
              <div className="flex flex-col items-center space-y-3">
                <div className="w-12 h-12 rounded-full bg-primary/5 border border-primary/20 flex items-center justify-center text-primary group-hover:scale-110 transition-transform duration-500 delay-100">
                  <LockKeyhole className="w-5 h-5" />
                </div>
                <span className="text-[10px] uppercase tracking-widest font-bold text-muted-foreground">
                  Permanence
                </span>
              </div>
              <div className="flex flex-col items-center space-y-3">
                <div className="w-12 h-12 rounded-full bg-primary/5 border border-primary/20 flex items-center justify-center text-primary group-hover:scale-110 transition-transform duration-500 delay-200">
                  <ShieldCheck className="w-5 h-5" />
                </div>
                <span className="text-[10px] uppercase tracking-widest font-bold text-muted-foreground">
                  Sanctuary
                </span>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Background decoration */}
      <div className="absolute -bottom-48 -right-48 w-96 h-96 bg-primary/10 rounded-full blur-[100px] pointer-events-none" />
      <div className="absolute top-1/4 -left-48 w-96 h-96 bg-primary/5 rounded-full blur-[100px] pointer-events-none" />

      {/* Ambiance */}
      <div className="fixed inset-0 pointer-events-none -z-10 bg-[radial-gradient(circle_at_center,transparent_0%,#000_100%)]" />
    </div>
  );
}
