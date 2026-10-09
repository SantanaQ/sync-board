import React, { type ChangeEvent, useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAuth } from "../../hooks/useAuth";
import { useApiError } from "../../hooks/useApiError";

import TempLogo from "../../assets/TempLogo";
import { FormButton } from "../../components/form/FormButton";
import { TextInput } from "../../components/form/TextInput";

export default function RegisterPage() {
    const [formData, setFormData] = useState({
        displayName: "",
        email: "",
        password: "",
        passwordConfirmation: "",
    });
    const [isSubmitting, setIsSubmitting] = useState(false);

    const { register } = useAuth();
    const navigate = useNavigate();

    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const handleRegister = async (
        event: React.SubmitEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        clearErrors();
        setIsSubmitting(true);

        try {
            await register({
                displayName: formData.displayName,
                email: formData.email,
                password: formData.password,
                passwordConfirmation: formData.passwordConfirmation,
            });

            navigate("/dashboard");
        } catch (error) {
            handleApiError(error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { id, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [id]: value,
        }));

        if (fieldErrors[id]) {
            clearFieldError(id);
        }
    };

    return (
        <main className="bg-app px-4 md:px-8">
            <div className="flex min-h-screen flex-col items-center justify-center py-12">
                <div className="w-full max-w-md">
                    <TempLogo className="mx-auto mb-8 block min-h-32 w-32" />

                    <div className="card p-6 md:p-8">
                        <h1 className="text-center text-2xl font-bold text-primary">
                            Sign up
                        </h1>
                        <p className="section-description text-center mt-1">
                            Create an account to get started.
                        </p>

                        <form
                            onSubmit={handleRegister}
                            className="mt-8 space-y-5"
                            noValidate
                        >
                            <TextInput
                                id="displayName"
                                label="Name"
                                type="text"
                                required
                                value={formData.displayName}
                                error={fieldErrors.displayName}
                                placeholder="Alice"
                                maxLength={100}
                                onChange={handleChange}
                                disabled={isSubmitting}
                            />

                            <TextInput
                                id="email"
                                label="Email"
                                type="email"
                                required
                                value={formData.email}
                                error={fieldErrors.email}
                                placeholder="alice@syncboard.com"
                                onChange={handleChange}
                                disabled={isSubmitting}
                            />

                            <TextInput
                                id="password"
                                label="Password"
                                type="password"
                                required
                                value={formData.password}
                                error={fieldErrors.password}
                                placeholder="••••••••"
                                onChange={handleChange}
                                disabled={isSubmitting}
                            />

                            <TextInput
                                id="passwordConfirmation"
                                label="Confirm password"
                                type="password"
                                required
                                value={formData.passwordConfirmation}
                                error={fieldErrors.passwordConfirmation}
                                placeholder="••••••••"
                                onChange={handleChange}
                                disabled={isSubmitting}
                            />

                            <FormButton
                                type="submit"
                                isLoading={isSubmitting}
                                loadingText="Signing up..."
                            >
                                Create account
                            </FormButton>

                            <div className="text-center text-sm text-secondary pt-2">
                                Already have an account?
                                <button
                                    type="button"
                                    onClick={() => navigate("/login")}
                                    className="ml-1 font-medium text-accent hover:underline focus:outline-none"
                                >
                                    Sign in
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    );
}