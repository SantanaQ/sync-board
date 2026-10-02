import {useCallback, useEffect, useState} from "react";

type UseCrudListOptions<T> = {
    fetchItems: () => Promise<T[]>;
    deleteItem?: (id: string) => Promise<void>;
};

export function useCrudList<T>({
                                   fetchItems,
                                   deleteItem,
                               }: UseCrudListOptions<T>) {
    const [items, setItems] = useState<T[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    const refresh = useCallback(async () => {
        setIsLoading(true);

        try {
            const result = await fetchItems();
            setItems(result);
        } finally {
            setIsLoading(false);
        }
    }, [fetchItems]);

    const remove = useCallback(
        async (id: string) => {
            if (!deleteItem) {
                throw new Error("deleteItem is not configured");
            }

            await deleteItem(id);
            await refresh();
        },
        [deleteItem, refresh],
    );

    useEffect(() => {
        refresh();
    }, [refresh]);

    return {
        items,
        isLoading,
        refresh,
        remove,
    };
}