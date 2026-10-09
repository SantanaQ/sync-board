import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import { useApiError } from "../../hooks/useApiError";

import TempLogo from "../../assets/TempLogo";
import { TextInput } from "../../components/form/TextInput";
import { FormButton } from "../../components/form/FormButton";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();

    const location = useLocation();

    const from = location.state?.from?.pathname ?? "/dashboard";

    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const handleLogin = async (
        event: React.FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        clearErrors();
        setIsSubmitting(true);

        try {
            await login({
                email,
                password,
            });

            navigate(from, { replace: true });
        } catch (error) {
            handleApiError(error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleEmailChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {
        setEmail(event.target.value);
        clearFieldError("email");
    };

    const handlePasswordChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {
        setPassword(event.target.value);
        clearFieldError("password");
    };

    return (
        <main className="bg-app px-4 md:px-8">
            <div className="flex min-h-screen flex-col items-center justify-center">
                <div className="w-full max-w-md">
                    <TempLogo className="mx-auto mb-8 block min-h-32 w-32" />

                    <div className="card p-6 md:p-8">
                        <h1 className="text-center text-2xl font-bold text-primary">
                            Sign in
                        </h1>
                        <p className="section-description text-center mt-1">
                            Welcome back! Please enter your details.
                        </p>

                        <form
                            onSubmit={handleLogin}
                            className="mt-8 space-y-5"
                            noValidate
                        >
                            <TextInput
                                id="email"
                                label="Email"
                                type="email"
                                required
                                value={email}
                                error={fieldErrors.email}
                                placeholder="alice@syncboard.com"
                                onChange={handleEmailChange}
                                disabled={isSubmitting}
                            />

                            <TextInput
                                id="password"
                                label="Password"
                                type="password"
                                required
                                value={password}
                                error={fieldErrors.password}
                                placeholder="••••••••"
                                onChange={handlePasswordChange}
                                disabled={isSubmitting}
                            />

                            <FormButton
                                type="submit"
                                isLoading={isSubmitting}
                                loadingText="Signing in..."
                            >
                                Sign in
                            </FormButton>

                            <div className="text-center text-sm text-secondary pt-2">
                                Don't have an account?
                                <button
                                    type="button"
                                    onClick={() => navigate("/register")}
                                    className="ml-1 font-medium text-accent hover:underline focus:outline-none"
                                >
                                    Sign up
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    );
}