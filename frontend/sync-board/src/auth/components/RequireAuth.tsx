import { Navigate, Outlet, useLocation } from "react-router-dom";
import {useAuth} from "../../hooks/useAuth.ts";

export function RequireAuth() {
    const { authStatus } = useAuth();
    const location = useLocation();

    if (authStatus === "loading") {
        return (
            <div className="flex min-h-screen items-center justify-center">
                Loading...
            </div>
        );
    }

    if (authStatus === "unauthenticated") {
        return (
            <Navigate
                to="/login"
                replace
                state={{ from: location }}
            />
        );
    }

    return <Outlet />;
}