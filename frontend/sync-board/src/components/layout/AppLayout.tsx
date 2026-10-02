
import { useEffect, useState } from "react";
import { Menu } from "lucide-react";
import { Outlet } from "react-router-dom";

import Sidebar from "./Sidebar";

export default function AppLayout() {
    const [collapsed, setCollapsed] = useState(false);
    const [mobileOpen, setMobileOpen] = useState(false);

    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth >= 1024) {
                setMobileOpen(false);
            }
        };

        window.addEventListener("resize", handleResize);

        return () => {
            window.removeEventListener("resize", handleResize);
        };
    }, []);

    useEffect(() => {
        document.body.style.overflow = mobileOpen
            ? "hidden"
            : "";

        return () => {
            document.body.style.overflow = "";
        };
    }, [mobileOpen]);

    return (
        <div className="min-h-screen bg-app">
            <Sidebar
                collapsed={collapsed}
                onCollapsedChange={setCollapsed}
                mobileOpen={mobileOpen}
                onMobileOpenChange={setMobileOpen}
            />

            <main
                className={`
min-h-screen
transition-[padding]
duration-200
ease-out

${
    collapsed
        ? "lg:pl-[76px]"
        : "lg:pl-[264px]"
}
`}
            >
                {/* Mobile header */}
                <header className="flex h-16 items-center border-b border-border bg-surface px-4 lg:hidden">
                    <button
                        type="button"
                        onClick={() => setMobileOpen(true)}
                        className="icon-button"
                        aria-label="Open sidebar"
                    >
                        <Menu className="h-5 w-5" />
                    </button>

                    <span className="ml-3 text-sm font-semibold text-primary">
                        Syncboard
                    </span>
                </header>

                <Outlet />
            </main>
        </div>
    );
}

