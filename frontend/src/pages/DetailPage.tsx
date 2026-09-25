import { Link, useParams } from 'react-router-dom';
import { useCatalog } from '../context/CatalogContext';
import { useFavorites } from '../context/FavoritesContext';
import { budgetLabel } from '../lib/discovery';
import Scene from '../components/Scene';
import CatalogStatus from '../components/CatalogStatus';
export default function DetailPage() {
  const { id } = useParams();
  const { items, loading } = useCatalog();
  const { ids, toggle } = useFavorites();
  const item = items.find((x) => x.id === Number(id));
  if (loading)
    return (
      <main id="main">
        <CatalogStatus />
      </main>
    );
  if (!item)
    return (
      <main id="main" className="empty-state">
        <h1>Experience not found.</h1>
        <Link className="primary" to="/explore">
          Back to explore
        </Link>
      </main>
    );
  return (
    <main id="main">
      <Link className="text-link" to="/explore">
        ← Back to experiences
      </Link>
      <CatalogStatus />
      <section className="detail-layout">
        <div>
          <p className="eyebrow">
            {item.category} · {item.neighborhood}
          </p>
          <h1>{item.name}</h1>
          <p className="detail-description">{item.description}</p>
          <div className="detail-facts">
            <div>
              <small>BUDGET</small>
              <strong>{budgetLabel(item.budgetLevel)}</strong>
            </div>
            <div>
              <small>MAKE TIME FOR</small>
              <strong>{item.durationMinutes} minutes</strong>
            </div>
            <div>
              <small>THE MOOD</small>
              <strong>{item.mood}</strong>
            </div>
          </div>
          <button
            className="primary"
            aria-pressed={ids.includes(item.id)}
            onClick={() => toggle(item.id)}
          >
            {ids.includes(item.id) ? '♥ Saved · Remove' : '♡ Save this experience'}
          </button>
          <p className="filter-note">
            This is a curated sample idea, not a live listing. Check current venue details,
            accessibility, prices, and opening hours before visiting.
          </p>
        </div>
        <div className="detail-art">
          <Scene />
        </div>
      </section>
    </main>
  );
}
