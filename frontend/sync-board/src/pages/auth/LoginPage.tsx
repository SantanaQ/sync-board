import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import { useApiError } from "../../hooks/useApiError";

import TempLogo from "../../assets/TempLogo";
import {TextInput} from "../../components/form/TextInput.tsx";
import {FormButton} from "../../components/form/FormButton.tsx";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();

    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const handleLogin = async (
        event: React.SubmitEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        clearErrors();
        setIsSubmitting(true);

        try {
            await login({
                email,
                password,
            });

            navigate("/welcome");
        } catch (error) {
            handleApiError(error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleEmailChange = (
        event: React.ChangeEvent<HTMLInputElement>,
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
        <main className="bg-gray-50 px-4 dark:bg-neutral-900 md:px-8">
            <div className="flex min-h-screen flex-col items-center justify-center">
                <div className="w-full max-w-md">
                    <TempLogo
                        className="
                            mx-auto
                            mb-8
                            block
                            min-h-32
                            w-32
                        "
                    />

                    <div
                        className="
                            rounded-lg
                            border
                            border-slate-300
                            bg-white
                            p-6
                            shadow-xs
                            dark:border-neutral-700
                            dark:bg-neutral-800
                            md:p-8
                        "
                    >
                        <h1
                            className="
                                text-center
                                text-3xl
                                font-bold
                                text-slate-900
                                dark:text-slate-50
                            "
                        >
                            Sign in
                        </h1>

                        <form
                            onSubmit={handleLogin}
                            className="mt-10 space-y-6"
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

                            <div
                                className="
                                    text-center
                                    text-sm
                                    text-slate-900
                                    dark:text-slate-50
                                "
                            >
                                Don't have an account?

                                <button
                                    type="button"
                                    onClick={() =>
                                        navigate("/register")
                                    }
                                    className="
                                        ml-1
                                        rounded
                                        font-medium
                                        text-blue-700
                                        hover:underline
                                        focus:outline-none
                                        focus-visible:ring-2
                                        focus-visible:ring-blue-500
                                        dark:text-blue-500
                                    "
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