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
import { Loader2 } from "lucide-react";
import api from "@/lib/api";
import { useAuthStore } from "@/store/authStore";

const registerSchema = z
  .object({
    name: z.string().min(2, "Name must be at least 2 characters"),
    email: z.string().email("Invalid email address"),
    password: z.string().min(6, "Password must be at least 6 characters"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

type RegisterFormValues = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const setAuth = useAuthStore((state) => state.setAuth);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormValues) => {
    setIsLoading(true);
    try {
      const { confirmPassword, ...registerData } = data;
      const response = await api.post("/auth/register", registerData);
      setAuth(response.data.user, response.data.token);
      router.push("/dashboard");
    } catch (error) {
      console.error("Registration failed", error);
      // In a real app, show a toast here
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-6 relative overflow-hidden py-12">
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
            Establish Your Legacy
          </CardTitle>
          <CardDescription className="text-muted-foreground uppercase tracking-widest text-xs">
            Join the digital curated archives
          </CardDescription>
        </CardHeader>
        <CardContent className="pt-8 px-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div className="space-y-2">
              <Label
                htmlFor="name"
                className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
              >
                Full Name
              </Label>
              <Input
                id="name"
                placeholder="The Curator"
                {...register("name")}
                className="bg-white/5 border-white/10 text-foreground placeholder:text-muted-foreground/30 focus:border-primary/50 transition-colors"
                disabled={isLoading}
              />
              {errors.name && (
                <p className="text-xs text-destructive">
                  {errors.name.message}
                </p>
              )}
            </div>
            <div className="space-y-2">
              <Label
                htmlFor="email"
                className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
              >
                Identity (Email)
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
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label
                  htmlFor="password"
                  className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
                >
                  Secret Key
                </Label>
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
              <div className="space-y-2">
                <Label
                  htmlFor="confirmPassword"
                  className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
                >
                  Repeat Key
                </Label>
                <Input
                  id="confirmPassword"
                  type="password"
                  {...register("confirmPassword")}
                  className="bg-white/5 border-white/10 text-foreground focus:border-primary/50 transition-colors"
                  disabled={isLoading}
                />
                {errors.confirmPassword && (
                  <p className="text-xs text-destructive">
                    {errors.confirmPassword.message}
                  </p>
                )}
              </div>
            </div>
            <Button
              type="submit"
              className="w-full gold-gradient text-primary-foreground font-bold uppercase tracking-widest py-6 mt-4"
              disabled={isLoading}
            >
              {isLoading ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                "Initiate Protocol"
              )}
            </Button>
          </form>
        </CardContent>
        <CardFooter className="flex flex-col items-center gap-4 pb-8 pt-4 border-t border-white/5 mx-6 mt-6">
          <p className="text-xs text-muted-foreground tracking-widest uppercase">
            Already hold a key?{" "}
            <Link
              href="/auth/login"
              className="text-primary hover:underline font-bold"
            >
              Enter the Vault
            </Link>
          </p>
        </CardFooter>
      </Card>

      {/* Background decoration */}
      <div className="absolute -bottom-48 -left-48 w-96 h-96 bg-primary/10 rounded-full blur-[100px] pointer-events-none" />
      <div className="absolute -top-48 -right-48 w-96 h-96 bg-primary/5 rounded-full blur-[100px] pointer-events-none" />
    </div>
  );
}
