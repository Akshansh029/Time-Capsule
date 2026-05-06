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
import { ArrowLeft, Loader2, Mail, ShieldCheck } from "lucide-react";
import api from "@/lib/api";
import { useAuthStore } from "@/store/authStore";
import { toast } from "sonner";

// Step 1: just email
const emailSchema = z.object({
  email: z.string().email("Invalid email address"),
});

// Step 2: full details + verification code
const registerSchema = z
  .object({
    name: z.string().min(3, "Name must be at least 3 characters").max(50),
    password: z
      .string()
      .min(6, "Password must be at least 6 characters")
      .max(50),
    confirmPassword: z.string(),
    verificationCode: z.string().min(1, "Verification code is required"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ["confirmPassword"],
  });

type EmailFormValues = z.infer<typeof emailSchema>;
type RegisterFormValues = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const [step, setStep] = useState<"email" | "details">("email");
  const [submittedEmail, setSubmittedEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const setAuth = useAuthStore((state) => state.setAuth);

  const emailForm = useForm<EmailFormValues>({
    resolver: zodResolver(emailSchema),
  });

  const registerForm = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  });

  // Step 1: check if email is available
  const onEmailSubmit = async (data: EmailFormValues) => {
    setIsLoading(true);
    try {
      const response = await api.post("/auth/check-email", null, {
        params: { email: data.email },
      });
      // response.data is boolean: false = already registered
      if (response.data === false) {
        emailForm.setError("email", {
          message: "This email is already registered. Please log in.",
        });
        return;
      }
      setSubmittedEmail(data.email);
      setStep("details");
      toast.success("Verification sent", {
        description: "A verification code has been dispatched to your email.",
      });
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setIsLoading(false);
    }
  };

  // Step 2: complete registration
  const onRegisterSubmit = async (data: RegisterFormValues) => {
    setIsLoading(true);
    try {
      const { confirmPassword, ...rest } = data;
      const response = await api.post("/auth/register", {
        ...rest,
        email: submittedEmail,
      });
      const { accessToken } = response.data;

      useAuthStore.getState().setAccessToken(accessToken);
      const userResponse = await api.get("/users/me");
      setAuth(userResponse.data, accessToken);

      toast.success("Legacy established", {
        description: "Your vault is ready for memories.",
      });
      router.push("/dashboard");
    } catch (error) {
      // Error handled by interceptor
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
          <div className="flex items-center justify-center gap-3 mb-2">
            <div
              className={`w-8 h-8 rounded-full flex items-center justify-center text-[10px] font-bold border transition-all duration-500 ${
                step === "email"
                  ? "bg-primary text-primary-foreground border-primary"
                  : "bg-primary/20 text-primary border-primary/40"
              }`}
            >
              1
            </div>
            <div className="h-px w-8 bg-white/10" />
            <div
              className={`w-8 h-8 rounded-full flex items-center justify-center text-[10px] font-bold border transition-all duration-500 ${
                step === "details"
                  ? "bg-primary text-primary-foreground border-primary"
                  : "border-white/10 text-muted-foreground"
              }`}
            >
              2
            </div>
          </div>
          <CardTitle className="font-serif text-3xl text-foreground">
            Establish Your Legacy
          </CardTitle>
          <CardDescription className="text-muted-foreground uppercase tracking-widest text-xs">
            {step === "email"
              ? "Enter your email to begin"
              : "Verify your identity and set credentials"}
          </CardDescription>
        </CardHeader>

        <CardContent className="pt-8 px-6">
          {step === "email" ? (
            <form
              onSubmit={emailForm.handleSubmit(onEmailSubmit)}
              className="space-y-6"
            >
              <div className="space-y-2">
                <Label
                  htmlFor="email"
                  className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
                >
                  Identity (Email)
                </Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground/50" />
                  <Input
                    id="email"
                    type="email"
                    placeholder="archivist@eternity.com"
                    {...emailForm.register("email")}
                    className="bg-white/5 border-white/10 text-foreground placeholder:text-muted-foreground/30 focus:border-primary/50 transition-colors pl-10"
                    disabled={isLoading}
                  />
                </div>
                {emailForm.formState.errors.email && (
                  <p className="text-xs text-destructive">
                    {emailForm.formState.errors.email.message}
                  </p>
                )}
              </div>
              <Button
                type="submit"
                className="w-full gold-gradient text-primary-foreground font-bold uppercase tracking-widest py-6 mt-4"
                disabled={isLoading}
              >
                {isLoading ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                  "Continue"
                )}
              </Button>
            </form>
          ) : (
            <form
              onSubmit={registerForm.handleSubmit(onRegisterSubmit)}
              className="space-y-5"
            >
              {/* Email display (read-only) */}
              <div className="flex items-center gap-3 px-4 py-3 rounded-xl bg-primary/5 border border-primary/10">
                <Mail className="w-4 h-4 text-primary/60 flex-shrink-0" />
                <span className="text-sm text-foreground/80 truncate">
                  {submittedEmail}
                </span>
                <button
                  type="button"
                  onClick={() => {
                    setStep("email");
                    registerForm.reset();
                  }}
                  className="ml-auto text-[10px] uppercase tracking-widest text-primary/60 hover:text-primary transition-colors flex-shrink-0"
                >
                  Change
                </button>
              </div>

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
                  {...registerForm.register("name")}
                  className="bg-white/5 border-white/10 text-foreground placeholder:text-muted-foreground/30 focus:border-primary/50 transition-colors"
                  disabled={isLoading}
                />
                {registerForm.formState.errors.name && (
                  <p className="text-xs text-destructive">
                    {registerForm.formState.errors.name.message}
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
                    {...registerForm.register("password")}
                    className="bg-white/5 border-white/10 text-foreground focus:border-primary/50 transition-colors"
                    disabled={isLoading}
                  />
                  {registerForm.formState.errors.password && (
                    <p className="text-xs text-destructive">
                      {registerForm.formState.errors.password.message}
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
                    {...registerForm.register("confirmPassword")}
                    className="bg-white/5 border-white/10 text-foreground focus:border-primary/50 transition-colors"
                    disabled={isLoading}
                  />
                  {registerForm.formState.errors.confirmPassword && (
                    <p className="text-xs text-destructive">
                      {registerForm.formState.errors.confirmPassword.message}
                    </p>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <Label
                  htmlFor="verificationCode"
                  className="text-xs uppercase tracking-widest text-muted-foreground font-bold"
                >
                  Verification Code
                </Label>
                <div className="relative">
                  <ShieldCheck className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground/50" />
                  <Input
                    id="verificationCode"
                    placeholder="Enter code from your email"
                    {...registerForm.register("verificationCode")}
                    className="bg-white/5 border-white/10 text-foreground placeholder:text-muted-foreground/30 focus:border-primary/50 transition-colors pl-10 tracking-widest"
                    disabled={isLoading}
                  />
                </div>
                {registerForm.formState.errors.verificationCode && (
                  <p className="text-xs text-destructive">
                    {registerForm.formState.errors.verificationCode.message}
                  </p>
                )}
                <p className="text-[10px] text-muted-foreground/60 uppercase tracking-wider">
                  Check your inbox for a verification code
                </p>
              </div>

              <Button
                type="submit"
                className="w-full gold-gradient text-primary-foreground font-bold uppercase tracking-widest py-6 mt-2"
                disabled={isLoading}
              >
                {isLoading ? (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                  "Seal the Covenant"
                )}
              </Button>
            </form>
          )}
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
