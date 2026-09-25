import { useSearchParams } from 'react-router-dom';
import { useCatalog } from '../context/CatalogContext';
import { categories, filterExperiences, moods } from '../lib/discovery';
import ExperienceCard from '../components/ExperienceCard';
import CatalogStatus from '../components/CatalogStatus';
export default function ExplorePage() {
  const [params, setParams] = useSearchParams();
  const { items, loading } = useCatalog();
  const filtered = filterExperiences(items, params);
  const change = (key: string, value: string) => {
    const next = new URLSearchParams(params);
    if (value) next.set(key, value);
    else next.delete(key);
    setParams(next, { replace: true });
  };
  return (
    <main id="main">
      <div className="page-heading">
        <p className="eyebrow">FIND YOUR CHICAGO</p>
        <h1>A city full of possibilities.</h1>
        <p>Start with a feeling. We’ll help you find the moment.</p>
      </div>
      <div className="explore-layout">
        <aside className="filter-panel" aria-label="Experience filters">
          <div className="filter-title">
            <h2>Make it yours</h2>
            <button className="text-button" onClick={() => setParams({})}>
              Reset all
            </button>
          </div>
          <label>
            Search
            <input
              type="search"
              placeholder="Place, neighborhood, idea…"
              maxLength={100}
              value={params.get('q') || ''}
              onChange={(e) => change('q', e.target.value)}
            />
          </label>
          <label>
            Your mood
            <select
              value={params.get('mood') || ''}
              onChange={(e) => change('mood', e.target.value)}
            >
              <option value="">Any mood</option>
              {moods.map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
          </label>
          <label>
            Category
            <select
              value={params.get('category') || ''}
              onChange={(e) => change('category', e.target.value)}
            >
              <option value="">All categories</option>
              {categories.map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
          </label>
          <label>
            Budget
            <select
              value={params.get('maxBudget') || ''}
              onChange={(e) => change('maxBudget', e.target.value)}
            >
              <option value="">Any budget</option>
              <option value="0">Free only</option>
              <option value="1">$ · Budget friendly</option>
              <option value="2">$$ · A little extra</option>
              <option value="3">$$$ · Treat yourself</option>
            </select>
          </label>
          <label>
            Time available
            <select
              value={params.get('maxDuration') || ''}
              onChange={(e) => change('maxDuration', e.target.value)}
            >
              <option value="">Any duration</option>
              <option value="60">Up to 1 hour</option>
              <option value="120">Up to 2 hours</option>
              <option value="180">Up to 3 hours</option>
            </select>
          </label>
          <label>
            Neighborhood
            <select
              value={params.get('neighborhood') || ''}
              onChange={(e) => change('neighborhood', e.target.value)}
            >
              <option value="">Anywhere in Chicago</option>
              {[...new Set(items.map((x) => x.neighborhood))].sort().map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
          </label>
          <p className="filter-note">
            Sample ideas for inspiration. Budgets and durations are illustrative.
          </p>
        </aside>
        <section className="results">
          <CatalogStatus />
          <div className="results-toolbar">
            <p role="status">
              <strong>{filtered.length}</strong> experiences to explore
            </p>
            <label className="sort-label">
              Sort by
              <select
                value={params.get('sort') || 'name'}
                onChange={(e) => change('sort', e.target.value)}
              >
                <option value="name">Name</option>
                <option value="budget">Lowest budget</option>
                <option value="duration">Shortest visit</option>
              </select>
            </label>
          </div>
          <div className="experience-grid two-col">
            {filtered.map((item) => (
              <ExperienceCard key={item.id} item={item} />
            ))}
          </div>
          {!loading && !filtered.length && (
            <div className="empty-state">
              <span>✧</span>
              <h2>A different kind of adventure?</h2>
              <p>No experiences match these filters. Try widening your search.</p>
              <button className="primary" onClick={() => setParams({})}>
                Clear filters
              </button>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
