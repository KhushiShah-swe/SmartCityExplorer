import { Link } from 'react-router-dom';
import { useFavorites } from '../context/FavoritesContext';
import { useCatalog } from '../context/CatalogContext';
import ExperienceCard from '../components/ExperienceCard';
import CatalogStatus from '../components/CatalogStatus';
export default function FavoritesPage() {
  const { ids } = useFavorites();
  const { items, loading } = useCatalog();
  const saved = items.filter((x) => ids.includes(x.id));
  return (
    <main id="main">
      <div className="page-heading">
        <p className="eyebrow">YOUR LITTLE CITY BUCKET LIST</p>
        <h1>Good plans, kept close.</h1>
        <p>All the places you want to get back to. Saved on this browser.</p>
      </div>
      <CatalogStatus />
      <div className="experience-grid">
        {saved.map((item) => (
          <ExperienceCard key={item.id} item={item} />
        ))}
      </div>
      {!loading && !saved.length && (
        <div className="empty-state">
          <span>♡</span>
          <h2>Your next favorite is out there.</h2>
          <p>Tap the heart on any experience to start your collection.</p>
          <Link className="primary" to="/explore">
            Find an experience ↗
          </Link>
        </div>
      )}
    </main>
  );
}
