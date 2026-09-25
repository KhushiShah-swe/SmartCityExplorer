import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import samples from '../../../shared/experiences.json';
import type { Experience } from '../types/Experience';
import { getExperiences } from '../services/api';
const Context = createContext<{
  items: Experience[];
  loading: boolean;
  demo: boolean;
  retry: () => void;
}>({ items: [], loading: true, demo: false, retry: () => {} });
export function CatalogProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<Experience[]>([]);
  const [loading, setLoading] = useState(true);
  const [demo, setDemo] = useState(false);
  const [attempt, setAttempt] = useState(0);
  useEffect(() => {
    const controller = new AbortController();
    let active = true;
    setLoading(true);
    const timeout = window.setTimeout(() => controller.abort(), 6000);
    getExperiences(controller.signal)
      .then((data) => {
        if (active) {
          setItems(data);
          setDemo(false);
        }
      })
      .catch(() => {
        if (active) {
          setItems(samples);
          setDemo(true);
        }
      })
      .finally(() => {
        if (active) setLoading(false);
        window.clearTimeout(timeout);
      });
    return () => {
      active = false;
      window.clearTimeout(timeout);
      controller.abort();
    };
  }, [attempt]);
  return (
    <Context.Provider value={{ items, loading, demo, retry: () => setAttempt((x) => x + 1) }}>
      {children}
    </Context.Provider>
  );
}
export const useCatalog = () => useContext(Context);
