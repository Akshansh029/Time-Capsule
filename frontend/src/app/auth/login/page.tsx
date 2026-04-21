"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Shield, Loader2 } from "lucide-react";
import api from "@/lib/api";
import { useAuthStore } from "@/store/authStore";
import { toast } from "sonner";

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const setAuth = useAuthStore((state) => state.setAuth);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormValues) => {
    setIsLoading(true);
    try {
      const response = await api.post("/auth/login", data);
      const { accessToken } = response.data;

      // Store token first to allow subsequent requests
      useAuthStore.getState().setAccessToken(accessToken);

      // Fetch user profile
      const userResponse = await api.get("/users/me");
      const user = userResponse.data;

      setAuth(user, accessToken);

      toast.success("Archive unlocked", {
        description: "Welcome back, Curator.",
      });
      router.push("/dashboard");
    } catch (error) {
      // Error is handled by interceptor
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-6 relative overflow-hidden">
      <div className="absolute top-0 left-0 w-full p-8">
        <Link
          href="/"
          className="font-serif text-xl font-bold text-primary tracking-widest"
        >
          ETERNAL VAULT
        </Link>
      </div>

      <Card className="w-full max-w-md glass border-none shadow-2xl relative z-10">
        <CardHeader className="space-y-2 text-center pb-8 border-b border-white/5 mx-6">
          <CardTitle className="font-serif text-3xl text-foreground">
            Return to your Vault
          </CardTitle>
          <CardDescription className="text-muted-foreground uppercase tracking-widest text-xs">
            Authenticate to access your memories
          </CardDescription>
        </CardHeader>
        <CardContent className="pt-8 px-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="space-y-2">
              <Label
                htmlFor="email"
                className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
              >
                Email Address
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="archivist@eternity.com"
                {...register("email")}
                className="bg-white/5 border-white/10 text-foreground placeholder:text-muted-foreground/30 focus:border-primary/50 transition-colors"
                disabled={isLoading}
              />
              {errors.email && (
                <p className="text-xs text-destructive">
                  {errors.email.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label
                  htmlFor="password"
                  className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
                >
                  Secret Key
                </Label>
                <Link
                  href="#"
                  className="text-[10px] uppercase tracking-widest text-primary hover:underline"
                >
                  Lost access?
                </Link>
              </div>
              <Input
                id="password"
                type="password"
                {...register("password")}
                className="bg-white/5 border-white/10 text-foreground focus:border-primary/50 transition-colors"
                disabled={isLoading}
              />
              {errors.password && (
                <p className="text-xs text-destructive">
                  {errors.password.message}
                </p>
              )}
            </div>
            <Button
              type="submit"
              className="w-full gold-gradient text-primary-foreground font-bold uppercase tracking-widest py-6"
              disabled={isLoading}
            >
              {isLoading ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                "Unlock Archive"
              )}
            </Button>
          </form>

          <div className="relative my-8">
            <div className="absolute inset-0 flex items-center">
              <span className="w-full border-t border-white/5" />
            </div>
            <div className="relative flex justify-center text-xs uppercase tracking-widest">
              <span className="bg-[#131313] px-4 text-muted-foreground">
                Digital Identity
              </span>
            </div>
          </div>

          <Button
            variant="outline"
            className="w-full border-white/10 hover:bg-white/5 text-xs uppercase tracking-widest py-6"
            disabled={isLoading}
          >
            <Shield className="mr-2 h-4 w-4" /> Sign In with Google
          </Button>
        </CardContent>
        <CardFooter className="flex flex-col items-center gap-4 pb-8 pt-4">
          <p className="text-xs text-muted-foreground tracking-widest uppercase">
            New to the Vault?{" "}
            <Link
              href="/auth/register"
              className="text-primary hover:underline font-bold"
            >
              Establish Legacy
            </Link>
          </p>
        </CardFooter>
      </Card>

      {/* Background decoration */}
      <div className="absolute -bottom-48 -right-48 w-96 h-96 bg-primary/10 rounded-full blur-[100px] pointer-events-none" />
      <div className="absolute -top-48 -left-48 w-96 h-96 bg-primary/5 rounded-full blur-[100px] pointer-events-none" />
    </div>
  );
}
