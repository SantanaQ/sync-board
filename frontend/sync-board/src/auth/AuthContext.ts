import { createContext } from "react";
import type { AuthContextValue } from "./authStore";

export const AuthContext = createContext<AuthContextValue | undefined>(
    undefined
);