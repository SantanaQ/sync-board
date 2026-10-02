import TempLogo from "../../assets/TempLogo.tsx";
import ProfileIcon from "../../assets/ProfileIcon.tsx";
import { useAuth } from "../../hooks/useAuth.ts";

import {
    FolderOpenDot,
    House,
    LogOut,
    PanelLeftClose,
    PanelLeftOpen,
    Settings2,
    X,
} from "lucide-react";

import { NavLink } from "react-router-dom";

type SidebarProps = {
    collapsed: boolean;
    onCollapsedChange: (collapsed: boolean) => void;

    mobileOpen: boolean;
    onMobileOpenChange: (open: boolean) => void;
};

const sidebarItems = [
    {
        name: "Dashboard",
        href: "/dashboard",
        icon: House,
    },
    {
        name: "Projects",
        href: "/projects",
        icon: FolderOpenDot,
    },
    {
        name: "Settings",
        href: "/settings",
        icon: Settings2,
    },
];

export default function Sidebar({
    collapsed,
    onCollapsedChange,
    mobileOpen,
    onMobileOpenChange,
}: SidebarProps) {
    const { user, logout } = useAuth();

    const closeMobileSidebar = () => {
        onMobileOpenChange(false);
    };

    return (
        <>
            {/* Mobile backdrop */}
            {mobileOpen && (
                <button
                    type="button"
                    aria-label="Sidebar schließen"
                    onClick={closeMobileSidebar}
                    className="
                        fixed
                        inset-0
                        z-40
                        bg-black/30
                        lg:hidden
                    "
                />
            )}

            <aside
                className={`
                    fixed
                    inset-y-0
                    left-0
                    z-50
                    
                    flex
                    flex-col
                    
                    border-r
                    border-border
                    
                    bg-surface
                    
                    shadow-xl
                    
                    transition-all
                    duration-200
                    ease-out
                    
                    w-[264px]
                    
                    ${
                        collapsed
                            ? "lg:w-[76px]"
                            : "lg:w-[264px]"
                    }
                    
                    ${
                        mobileOpen
                            ? "translate-x-0"
                            : "-translate-x-full lg:translate-x-0"
                    }
                `}
            >
                {/* Header */}
                <div
                    className={`
                        flex
                        h-20
                        shrink-0
                        items-center
                        border-b
                        border-border
                        px-4
                        
                        ${
                            collapsed
                                ? "lg:justify-center"
                                : "justify-between"
                        }
                        `}
                                        >
                                            <div
                                                className={`
                        flex
                        items-center
                        
                        ${
                            collapsed
                                ? "lg:justify-center"
                                : "gap-3"
                        }
                    `}
                    >
                        <TempLogo className="h-8 w-8 shrink-0" />

                        {!collapsed && (
                            <span className="text-sm font-semibold tracking-tight text-primary">
                                Syncboard
                            </span>
                        )}
                    </div>

                    {/* Mobile close */}
                    <button
                        type="button"
                        onClick={closeMobileSidebar}
                        className="icon-button lg:hidden"
                        aria-label="Sidebar schließen"
                    >
                        <X className="h-5 w-5" />
                    </button>

                    {/* Desktop collapse */}
                    <button
                        type="button"
                        onClick={() =>
                            onCollapsedChange(!collapsed)
                        }
                        className="icon-button hidden lg:inline-flex"
                        aria-label={
                            collapsed
                                ? "Sidebar ausklappen"
                                : "Sidebar einklappen"
                        }
                    >
                        {collapsed ? (
                            <PanelLeftOpen className="h-4 w-4" />
                        ) : (
                            <PanelLeftClose className="h-4 w-4" />
                        )}
                    </button>
                </div>

                {/* Navigation */}
                <nav
                    aria-label="Primary sidebar navigation"
                    className="flex-1 overflow-y-auto px-3 py-5"
                >
                    <div
                        className={`
                            mb-3
                            px-2
                            text-[11px]
                            font-semibold
                            uppercase
                            tracking-wider
                            text-muted
                            
                            ${collapsed ? "lg:hidden" : ""}
                        `}
                    >
                        Navigation
                    </div>

                    <ul className="space-y-1">
                        {sidebarItems.map((item) => {
                            const Icon = item.icon;

                            return (
                                <li key={item.href}>
                                    <NavLink
                                        to={item.href}
                                        onClick={closeMobileSidebar}
                                        title={
                                            collapsed
                                                ? item.name
                                                : undefined
                                        }
                                        className={({ isActive }) => `
                                            group
                                            flex
                                            items-center
                                            rounded-lg
                                            py-2.5
                                            text-sm
                                            font-medium
                                            transition-colors
                                            
                                            ${
                                                collapsed
                                                    ? "lg:justify-center lg:px-0"
                                                    : "gap-3 px-3"
                                            }
                                            
                                            ${
                                                isActive
                                                    ? "bg-accent-soft text-accent"
                                                    : "text-secondary hover:bg-surface-muted hover:text-primary"
                                            }
                                        `}
                                    >
                                        {({ isActive }) => (
                                            <>
                                                <Icon
                                                    className={`
                                                        h-[18px]
                                                        w-[18px]
                                                        shrink-0
                                                        
                                                        ${
                                                            isActive
                                                                ? "text-accent"
                                                                : "text-muted group-hover:text-primary"
                                                        }
                                                    `}
                                                    strokeWidth={
                                                        isActive
                                                            ? 2.2
                                                            : 1.8
                                                    }
                                                />

                                                <span
                                                    className={
                                                        collapsed
                                                            ? "lg:hidden"
                                                            : undefined
                                                    }
                                                >
                                                    {item.name}
                                                </span>
                                            </>
                                        )}
                                    </NavLink>
                                </li>
                            );
                        })}
                    </ul>
                </nav>

                {/* User */}
                <div className="border-t border-border p-3">
                    <div
                        className={`
                            flex
                            items-center
                            rounded-lg
                            p-2
                            
                            ${
                                collapsed
                                    ? "lg:justify-center"
                                    : "gap-3"
                            }
                        `}
                    >
                        <ProfileIcon className="h-9 w-9 shrink-0" />

                        {!collapsed && (
                            <>
                                <div className="min-w-0 flex-1">
                                    <p className="truncate text-sm font-medium text-primary">
                                        {user?.displayName ||
                                            "User"}
                                    </p>

                                    <p className="truncate text-xs text-muted">
                                        {user?.email}
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    onClick={logout}
                                    className="icon-button"
                                    title="Logout"
                                    aria-label="logout"
                                >
                                    <LogOut className="h-4 w-4" />
                                </button>
                            </>
                        )}
                    </div>
                </div>
            </aside>
        </>
    );
}

