import TempLogo from "../../assets/TempLogo.tsx";
import React, {type ChangeEvent, useState} from "react";
import {useAuth} from "../../hooks/useAuth.ts";
import {useNavigate} from "react-router-dom";
import {useApiError} from "../../hooks/useApiError.ts";
import {FormButton} from "../../components/form/FormButton.tsx";
import {TextInput} from "../../components/form/TextInput.tsx";

export default function RegisterPage() {

    const [formData, setFormData] = useState({
        displayName: "",
        email: "",
        password: "",
        passwordConfirmation: ""
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

            navigate("/welcome");

        } catch (error) {
            handleApiError(error);
        } finally {
            setIsSubmitting(false);
        }

    }

    const handleChange = (
        event: ChangeEvent<HTMLInputElement>
    ) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));

        if (fieldErrors[name]) {
            clearFieldError(name);
        }
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
                            Sign up
                        </h1>

                        <form
                            onSubmit={handleRegister}
                            className="mt-10 space-y-6"
                            noValidate
                        >
                            <TextInput
                                id="displayName"
                                label="name"
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

                            <div
                                className="
                                    text-center
                                    text-sm
                                    text-slate-900
                                    dark:text-slate-50
                                "
                            >
                                Already have an account?

                                <button
                                    type="button"
                                    onClick={() =>
                                        navigate("/login")
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