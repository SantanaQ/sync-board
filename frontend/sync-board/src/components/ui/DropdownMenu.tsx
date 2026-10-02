import {
    useLayoutEffect,
    useEffect,
    useRef,
    useState,
    type ReactNode,
} from "react";
import { createPortal } from "react-dom";
import { MoreHorizontal } from "lucide-react";

type DropdownMenuProps = {
    children: ReactNode;
};

type Position = {
    top: number;
    right: number;
};

export default function DropdownMenu({
                                         children,
                                     }: DropdownMenuProps) {
    const [open, setOpen] = useState(false);
    const [position, setPosition] = useState<Position | null>(null);

    const triggerRef = useRef<HTMLButtonElement>(null);
    const menuRef = useRef<HTMLDivElement>(null);

    const updatePosition = () => {
        if (!triggerRef.current) {
            return;
        }

        const rect = triggerRef.current.getBoundingClientRect();

        setPosition({
            top: rect.bottom + 4,
            right: window.innerWidth - rect.right,
        });
    };

    useLayoutEffect(() => {
        if (!open) {
            setPosition(null);
            return;
        }

        updatePosition();
    }, [open]);

    useEffect(() => {
        if (!open) {
            return;
        }

        const handleResize = () => {
            updatePosition();
        };

        const handleScroll = () => {
            updatePosition();
        };

        window.addEventListener("resize", handleResize);
        window.addEventListener("scroll", handleScroll, true);

        return () => {
            window.removeEventListener("resize", handleResize);
            window.removeEventListener("scroll", handleScroll, true);
        };
    }, [open]);

    useEffect(() => {
        if (!open) {
            return;
        }

        const handleClickOutside = (event: MouseEvent) => {
            const target = event.target as Node;

            if (
                triggerRef.current?.contains(target) ||
                menuRef.current?.contains(target)
            ) {
                return;
            }

            setOpen(false);
        };

        const handleEscape = (event: KeyboardEvent) => {
            if (event.key === "Escape") {
                setOpen(false);
            }
        };

        document.addEventListener(
            "mousedown",
            handleClickOutside
        );

        document.addEventListener(
            "keydown",
            handleEscape
        );

        return () => {
            document.removeEventListener(
                "mousedown",
                handleClickOutside
            );

            document.removeEventListener(
                "keydown",
                handleEscape
            );
        };
    }, [open]);

    return (
        <>
            <button
                ref={triggerRef}
                type="button"
                onClick={() => setOpen((value) => !value)}
                className="icon-button"
                aria-label="Options"
                aria-expanded={open}
            >
                <MoreHorizontal className="h-4 w-4" />
            </button>

            {open &&
                position &&
                createPortal(
                    <div
                        ref={menuRef}
                        className="
                            fixed
                            z-[9999]
                            min-w-[160px]
                            overflow-hidden
                            rounded-lg
                            border border-border
                            bg-surface
                            p-1
                            shadow-lg
                        "
                        style={{
                            top: position.top,
                            right: position.right,
                        }}
                    >
                        {children}
                    </div>,
                    document.body
                )}
        </>
    );
}