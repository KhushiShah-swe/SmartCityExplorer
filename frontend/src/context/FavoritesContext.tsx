import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { readFavoriteIds } from '../lib/discovery';
const Context = createContext<{
  ids: number[];
  toggle: (id: number) => void;
  storageError: boolean;
}>({ ids: [], toggle: () => {}, storageError: false });
export function FavoritesProvider({ children }: { children: ReactNode }) {
  const [ids, setIds] = useState<number[]>(() => {
    try {
      return readFavoriteIds(localStorage);
    } catch {
      return [];
    }
  });
  const [storageError, setStorageError] = useState(false);
  useEffect(() => {
    try {
      localStorage.setItem('sce:favorites:v1', JSON.stringify(ids));
      setStorageError(false);
    } catch {
      setStorageError(true);
    }
  }, [ids]);
  useEffect(() => {
    const sync = (event: StorageEvent) => {
      if (event.key === 'sce:favorites:v1' || event.key === null) {
        try {
          setIds(readFavoriteIds(localStorage));
        } catch {
          setStorageError(true);
        }
      }
    };
    window.addEventListener('storage', sync);
    return () => window.removeEventListener('storage', sync);
  }, []);
  return (
    <Context.Provider
      value={{
        ids,
        storageError,
        toggle: (id) =>
          setIds((current) =>
            current.includes(id) ? current.filter((x) => x !== id) : [...current, id],
          ),
      }}
    >
      {children}
    </Context.Provider>
  );
}
export const useFavorites = () => useContext(Context);
