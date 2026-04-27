"use client";
import React, { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import api from "@/lib/api";
import {
  LockedCapsuleDto,
  UnlockedCapsuleDto,
  CapsuleContentDto,
  CapsuleMemberDto,
} from "@/types/capsule";
import { Navbar } from "@/components/Navbar";
import CapsuleCountdown from "@/components/CapsuleCountdown";
import {
  ArrowLeft,
  Lock,
  Unlock,
  Clock,
  Shield,
  Calendar,
  User,
  Sparkles,
  Loader2,
  Globe,
  LockKeyhole,
  FileText,
  Info,
  Type,
  Image as ImageIcon,
  File as FileIcon,
  Download,
  Users,
} from "lucide-react";
import { format } from "date-fns";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Label } from "@/components/ui/label";

const CapsuleDetailsPage = () => {
  const params = useParams();
  const router = useRouter();
  const slug = params.slug as string;

  const [capsule, setCapsule] = useState<UnlockedCapsuleDto | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchCapsule = async () => {
    try {
      setIsLoading(true);
      const response = await api.get(`/capsules/${slug}`);
      console.log("Response: ", response.data);

      setCapsule(response.data);
      setError(null);
    } catch (err: any) {
      console.error("Failed to fetch capsule:", err);
      console.log("Error: ", err.response);

      setError(
        err.response?.data?.message || "Failed to retrieve the artifact.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (slug) {
      fetchCapsule();
    }
  }, [slug]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#131313] flex items-center justify-center">
        <div className="flex flex-col items-center space-y-4">
          <Loader2 className="w-12 h-12 text-primary animate-spin" />
          <p className="text-muted-foreground uppercase tracking-[0.3em] text-[10px]">
            Retrieving Artifact...
          </p>
        </div>
      </div>
    );
  }

  if (error || !capsule) {
    return (
      <div className="min-h-screen bg-[#131313] text-foreground font-sans">
        <Navbar />
        <main className="container mx-auto px-6 pt-40 pb-20 max-w-lg text-center">
          <div className="glass-variant p-10 rounded-[2rem] space-y-6">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-red-500/10 border border-red-500/20 mb-2">
              <Shield className="w-8 h-8 text-red-500/70" />
            </div>
            <h1 className="font-serif text-3xl">Access Denied</h1>
            <p className="text-muted-foreground text-sm leading-relaxed">
              {error ||
                "The artifact you seek could not be found or the temporal seals remain intact."}
            </p>
            <Button
              asChild
              className="w-full gold-gradient text-primary-foreground font-bold uppercase tracking-widest text-xs h-12 rounded-xl"
            >
              <Link href="/dashboard">Back to Archive</Link>
            </Button>
          </div>
        </main>
      </div>
    );
  }

  const isLocked = capsule.status === "LOCKED";

  const renderContent = (content: CapsuleContentDto) => {
    switch (content.type) {
      case "TEXT":
        return (
          <div className="p-6 rounded-2xl bg-white/5 border border-white/10 space-y-4">
            <div className="flex items-center space-x-3 text-primary/60">
              <Type className="w-4 h-4" />
              <span className="text-[10px] uppercase tracking-widest font-bold">
                Textual Testimony
              </span>
            </div>
            <p className="text-sm leading-relaxed text-foreground/90 whitespace-pre-wrap font-serif">
              {content.body}
            </p>
          </div>
        );
      case "IMAGE":
        return (
          <div className="rounded-2xl overflow-hidden bg-white/5 border border-white/10 group">
            <div className="relative aspect-video">
              <img
                src={content.fileUrl}
                alt="Archival Imagery"
                className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
              />
              <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                <Button
                  variant="outline"
                  className="rounded-full gold-gradient border-none text-primary-foreground"
                  size="icon"
                >
                  <Download className="w-4 h-4" />
                </Button>
              </div>
            </div>
            <div className="p-4 flex items-center justify-between border-t border-white/5">
              <div className="flex items-center space-x-2 text-primary/60">
                <ImageIcon className="w-3 h-3" />
                <span className="text-[9px] uppercase tracking-widest font-bold">
                  Spectral Visual
                </span>
              </div>
              <span className="text-[8px] text-muted-foreground">
                ID: {content.id.slice(0, 8)}
              </span>
            </div>
          </div>
        );
      case "FILE":
        return (
          <div className="p-5 rounded-2xl bg-white/5 border border-white/10 flex items-center justify-between group hover:bg-white/[0.08] transition-colors">
            <div className="flex items-center space-x-4">
              <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                <FileIcon className="w-5 h-5 text-primary" />
              </div>
              <div>
                <p className="text-xs font-medium uppercase tracking-wider">
                  Archival Artifact
                </p>
                <p className="text-[10px] text-muted-foreground truncate max-w-[200px]">
                  {content.fileUrl}
                </p>
              </div>
            </div>
            <Button
              variant="ghost"
              size="icon"
              className="text-muted-foreground hover:text-primary"
            >
              <Download className="w-4 h-4" />
            </Button>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-[#131313] text-foreground font-sans selection:bg-primary/30">
      <Navbar />

      <main className="container mx-auto px-6 pt-32 pb-20 max-w-5xl">
        <Link
          href="/dashboard"
          className="inline-flex items-center text-xs uppercase tracking-widest text-muted-foreground hover:text-primary transition-colors mb-12 group"
        >
          <ArrowLeft className="w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform" />
          Back to Archive
        </Link>

        {isLocked ? (
          /* LOCKED STATE */
          <div className="space-y-16 animate-in fade-in duration-1000">
            <div className="flex flex-col items-center text-center space-y-6">
              <div className="relative">
                <div className="absolute inset-0 bg-primary/20 blur-3xl rounded-full scale-150 animate-pulse" />
                <div className="relative inline-flex items-center justify-center w-24 h-24 rounded-full bg-primary/10 border border-primary/20 backdrop-blur-3xl shadow-2xl">
                  <LockKeyhole className="w-10 h-10 text-primary animate-pulse" />
                </div>
              </div>

              <div className="space-y-4">
                <h1 className="font-serif text-4xl md:text-6xl tracking-tight">
                  The <span className="text-primary italic">Dormant</span>{" "}
                  Artifact
                </h1>
                <p className="text-muted-foreground uppercase tracking-[0.25em] text-[10px] max-w-md mx-auto leading-loose">
                  ONLY THE PASSAGE OF TIME CAN BREAK THE SACRED SEAL
                </p>
              </div>

              <div className="w-full max-w-2xl pt-4">
                <CapsuleCountdown
                  unlockDate={capsule.unlockDate}
                  onUnlock={() => fetchCapsule()}
                />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8 items-start">
              {/* Left Column: Info */}
              <div className="glass-variant p-10 rounded-[3rem] space-y-8 relative overflow-hidden group border-primary/10">
                <div className="absolute top-0 right-0 w-32 h-32 bg-primary/5 blur-[60px] -mr-16 -mt-16 rounded-full" />

                <div className="space-y-2">
                  <Label className="text-[10px] uppercase tracking-widest text-primary/50 font-bold">
                    Artifact Identifier
                  </Label>
                  <h2 className="text-2xl font-serif text-foreground truncate">
                    {capsule.title}
                  </h2>
                </div>

                <div className="space-y-3">
                  <Label className="text-[10px] uppercase tracking-widest text-primary/50 font-bold">
                    Archival Teaser
                  </Label>
                  <p className="text-sm text-muted-foreground leading-relaxed italic">
                    "{capsule.description}"
                  </p>
                </div>

                <div className="pt-4 border-t border-white/5 flex flex-col space-y-6">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                      <div className="w-8 h-8 rounded-full bg-white/5 flex items-center justify-center">
                        <User className="w-4 h-4 text-primary/60" />
                      </div>
                      <div>
                        <p className="text-[9px] uppercase tracking-widest text-muted-foreground">
                          Creator
                        </p>
                        <p className="text-xs font-medium">
                          {capsule.ownerName}
                        </p>
                      </div>
                    </div>
                    <Badge
                      className={
                        capsule.isPrivate
                          ? "bg-red-500/10 text-red-400 border-red-500/20"
                          : "bg-primary/10 text-primary border-primary/20"
                      }
                    >
                      {capsule.isPrivate ? "PRIVATE" : "PUBLIC"}
                    </Badge>
                  </div>

                  {capsule.capsuleMembers &&
                    capsule.capsuleMembers.length > 0 && (
                      <div className="pt-4 border-t border-white/5">
                        <div className="flex items-center space-x-3 text-primary/80 mb-4">
                          <Users className="w-4 h-4" />
                          <span className="text-[10px] uppercase tracking-widest font-bold">
                            Custodian Network
                          </span>
                        </div>
                        <div className="space-y-4">
                          {capsule.capsuleMembers.map((member) => (
                            <div
                              key={member.id}
                              className="flex items-center justify-between group"
                            >
                              <div className="flex items-center space-x-3">
                                <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary/60 border border-primary/20 group-hover:bg-primary group-hover:text-primary-foreground transition-all duration-500">
                                  <User className="w-3 h-3" />
                                </div>
                                <div className="flex-1 min-w-0">
                                  <p className="text-xs font-semibold text-foreground truncate">
                                    {member.name || member.email.split("@")[0]}
                                  </p>
                                  <p className="text-[8px] text-muted-foreground truncate uppercase tracking-tighter">
                                    {member.email}
                                  </p>
                                </div>
                              </div>
                              {/* <Badge
                                variant="outline"
                                className="text-[7px] h-5 border-white/10 text-muted-foreground group-hover:border-primary/30 group-hover:text-primary transition-colors uppercase"
                              >
                                {member.role}
                              </Badge> */}
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                </div>
              </div>

              {/* Right Column: Protocols */}
              <div className="space-y-6">
                <div className="glass-variant p-8 rounded-[2.5rem] space-y-6 border-white/5">
                  <div className="flex items-center space-x-3 text-primary/80">
                    <Shield className="w-5 h-5" />
                    <h3 className="text-xs uppercase tracking-[0.2em] font-bold">
                      Resurrection Protocols
                    </h3>
                  </div>

                  <div className="space-y-4">
                    <div className="flex items-start space-x-4">
                      <Clock className="w-4 h-4 text-muted-foreground mt-0.5" />
                      <div>
                        <p className="text-[11px] font-medium">Temporal Lock</p>
                        <p className="text-[10px] text-muted-foreground mt-0.5">
                          The contents remain encrypted until{" "}
                          {format(
                            new Date(capsule.unlockDate),
                            "MMMM do, yyyy 'at' p",
                          )}
                          .
                        </p>
                      </div>
                    </div>

                    <div className="flex items-start space-x-4">
                      <Calendar className="w-4 h-4 text-muted-foreground mt-0.5" />
                      <div>
                        <p className="text-[11px] font-medium">
                          Interment Date
                        </p>
                        <p className="text-[10px] text-muted-foreground mt-0.5">
                          Submitted to the vault on{" "}
                          {format(new Date(capsule.createdAt), "MMMM do, yyyy")}
                          .
                        </p>
                      </div>
                    </div>

                    <div className="flex items-start space-x-4">
                      <Info className="w-4 h-4 text-muted-foreground mt-0.5" />
                      <div>
                        <p className="text-[11px] font-medium">
                          Cryptographic Integrity
                        </p>
                        <p className="text-[10px] text-muted-foreground mt-0.5">
                          AES-256 archival encryption applied at source node.
                        </p>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="bg-primary/5 border border-primary/10 rounded-3xl p-6 flex gap-4">
                  <Sparkles className="w-6 h-6 text-primary flex-shrink-0" />
                  <div className="space-y-1">
                    <p className="text-[11px] font-serif text-primary italic">
                      The Wait of Ages
                    </p>
                    <p className="text-[10px] text-muted-foreground leading-relaxed">
                      This artifact is part of a larger collection. Its contents
                      are part of a legacy that transcends the present moment.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ) : (
          /* UNLOCKED STATE */
          <div className="space-y-12 animate-in fade-in zoom-in duration-1000">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              <div className="lg:col-span-2 space-y-8">
                <div className="glass-variant p-8 md:p-12 rounded-[3.5rem] border-primary/20 relative overflow-hidden group min-h-[300px]">
                  <div className="absolute top-0 right-0 w-64 h-64 bg-primary/5 blur-[100px] -mr-32 -mt-32 rounded-full" />

                  <div className="relative space-y-8">
                    <div className="flex items-center space-x-4 opacity-50">
                      <div className="h-px w-8 bg-gradient-to-r from-transparent to-primary" />
                      <span className="text-[9px] uppercase tracking-[0.3em] font-bold text-primary">
                        Original Manifest
                      </span>
                      <div className="h-px w-8 bg-gradient-to-l from-transparent to-primary" />
                    </div>

                    <div className="prose prose-invert max-w-none">
                      <p className="text-lg md:text-xl leading-relaxed text-foreground/90 font-light">
                        {capsule.description}
                      </p>
                    </div>
                  </div>
                </div>

                <div className="space-y-6">
                  <div className="flex items-center justify-between px-2">
                    <div className="space-y-1">
                      <h3 className="text-sm uppercase tracking-[0.2em] font-bold text-primary/80">
                        Archival Storage
                      </h3>
                      <p className="text-[9px] text-muted-foreground uppercase tracking-widest font-medium">
                        Decrypted Artifacts
                      </p>
                    </div>
                    <Badge className="bg-primary/10 text-primary border-primary/20 h-6 text-[9px]">
                      {capsule.contents?.length || 0} TOTAL
                    </Badge>
                  </div>

                  <div className="grid grid-cols-1 gap-6">
                    {capsule.contents?.map((content) => (
                      <div
                        key={content.id}
                        className="animate-in fade-in slide-in-from-bottom-4 duration-700"
                      >
                        {renderContent(content)}
                      </div>
                    ))}
                    {(!capsule.contents || capsule.contents.length === 0) && (
                      <div className="py-12 border border-dashed border-white/10 rounded-3xl text-center">
                        <p className="text-muted-foreground text-[10px] uppercase tracking-widest">
                          No additional artifacts interred
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              </div>

              <div className="space-y-8">
                <div className="glass-variant p-8 rounded-[2.5rem] border-white/5 space-y-6">
                  <div className="flex items-center space-x-3 text-primary/80">
                    <Users className="w-5 h-5" />
                    <h3 className="text-xs uppercase tracking-[0.2em] font-bold">
                      Custodian Network
                    </h3>
                  </div>

                  <div className="space-y-4">
                    {capsule.capsuleMembers?.map((member) => (
                      <div
                        key={member.id}
                        className="flex items-center justify-between group"
                      >
                        <div className="flex items-center space-x-3">
                          <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center text-primary border border-primary/20 group-hover:bg-primary group-hover:text-primary-foreground transition-all duration-500">
                            <User className="w-4 h-4" />
                          </div>
                          <div>
                            <p className="text-xs font-semibold">
                              {member.name || member.email.split("@")[0]}
                            </p>
                            <p className="text-[8px] text-muted-foreground uppercase tracking-tighter">
                              {member.email}
                            </p>
                          </div>
                        </div>
                        {/* <Badge
                          variant="outline"
                          className="text-[7px] h-5 border-white/10 text-muted-foreground group-hover:border-primary/30 group-hover:text-primary transition-colors"
                        >
                          {member.role}
                        </Badge> */}
                      </div>
                    ))}

                    <div className="flex items-center justify-between pt-2 border-t border-white/5">
                      <div className="flex items-center space-x-3 opacity-60">
                        <div className="w-9 h-9 rounded-full bg-white/5 flex items-center justify-center border border-white/5">
                          <User className="w-4 h-4 text-primary/40" />
                        </div>
                        <div>
                          <p className="text-xs font-semibold text-muted-foreground">
                            {capsule.ownerName} (Creator)
                          </p>
                          <p className="text-[8px] text-muted-foreground uppercase tracking-tighter font-bold">
                            Origin Node
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="bg-primary/5 border border-primary/10 rounded-2xl p-6 space-y-4">
                  <div className="flex items-center space-x-3 text-primary">
                    <Info className="w-4 h-4" />
                    <span className="text-[10px] uppercase tracking-widest font-bold">
                      Temporal Integrity
                    </span>
                  </div>
                  <div className="space-y-3">
                    <div className="flex justify-between text-[9px] uppercase tracking-widest">
                      <span className="text-muted-foreground">Intered On</span>
                      <span className="font-bold">
                        {format(new Date(capsule.createdAt), "MMM d, yyyy")}
                      </span>
                    </div>
                    <div className="flex justify-between text-[9px] uppercase tracking-widest">
                      <span className="text-muted-foreground">Revealed On</span>
                      <span className="font-bold text-primary">
                        {format(new Date(), "MMM d, yyyy")}
                      </span>
                    </div>
                    <div className="flex justify-between text-[9px] uppercase tracking-widest pt-2 border-t border-white/5">
                      <span className="text-muted-foreground">Visibility</span>
                      <span className="font-bold">
                        {capsule.isPrivate
                          ? "PRIVATE ARCHIVE"
                          : "PUBLIC RECORD"}
                      </span>
                    </div>
                  </div>
                </div>

                <Button
                  variant="outline"
                  className="w-full rounded-2xl border-white/10 hover:border-primary/30 hover:bg-primary/5 text-primary h-12 text-[10px] uppercase tracking-[0.2em] font-bold"
                >
                  <Download className="w-4 h-4 mr-2" />
                  Extract Manifest
                </Button>
              </div>
            </div>

            <div className="text-center italic text-muted-foreground/30 text-[10px] font-serif uppercase tracking-[0.4em] pt-12">
              — Archival Resurrection Complete —
            </div>
          </div>
        )}
      </main>

      {/* Ambiance */}
      <div className="fixed inset-0 pointer-events-none -z-10 bg-[radial-gradient(circle_at_center,transparent_0%,#000_100%)]" />
      <div className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full max-w-4xl max-h-[600px] pointer-events-none -z-20 opacity-20">
        <div className="absolute inset-0 bg-primary/10 blur-[150px] rounded-full" />
      </div>
    </div>
  );
};

export default CapsuleDetailsPage;
