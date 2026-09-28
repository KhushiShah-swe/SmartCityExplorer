import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useCatalog } from '../context/CatalogContext';
import { categories, filterExperiences, moods } from '../lib/discovery';
import { getLivePlaces } from '../services/api';
import type { Place } from '../types/Place';
import ExperienceCard from '../components/ExperienceCard';
import PlaceCard from '../components/PlaceCard';
import CatalogStatus from '../components/CatalogStatus';

export default function ExplorePage() {
  const [params, setParams] = useSearchParams();
  const { items, loading } = useCatalog();
  const [places, setPlaces] = useState<Place[]>([]);
  const [placesLoading, setPlacesLoading] = useState(false);
  const [placesError, setPlacesError] = useState(false);
  const filtered = filterExperiences(items, params);

  useEffect(() => {
    const controller = new AbortController();
    setPlacesLoading(true);
    setPlacesError(false);
    getLivePlaces(controller.signal, params.get('mood') || undefined, params.get('maxBudget') || undefined)
      .then(setPlaces)
      .catch((error) => {
        if (error?.name !== 'AbortError') setPlacesError(true);
      })
      .finally(() => setPlacesLoading(false));
    return () => controller.abort();
  }, [params.get('mood'), params.get('maxBudget')]);

  const change = (key: string, value: string) => {
    const next = new URLSearchParams(params);
    if (value) next.set(key, value);
    else next.delete(key);
    setParams(next, { replace: true });
  };

  const eventbriteQuery = encodeURIComponent(
    `${params.get('mood') || 'things to do'} Chicago`,
  );
  const eventbriteUrl = `https://www.eventbrite.com/d/il--chicago/${eventbriteQuery.replace(/%20/g, '-')}/`;

  return (
    <main id="main">
      <div className="page-heading">
        <p className="eyebrow">FIND YOUR CHICAGO</p>
        <h1>A city full of possibilities.</h1>
        <p>Discover real cafés, places, hidden spots and events that fit your mood.</p>
      </div>
      <div className="explore-layout">
        <aside className="filter-panel" aria-label="Experience filters">
          <div className="filter-title">
            <h2>Make it yours</h2>
            <button className="text-button" onClick={() => setParams({})}>Reset all</button>
          </div>
          <label>Search
            <input type="search" placeholder="Place, neighborhood, idea…" maxLength={100}
              value={params.get('q') || ''} onChange={(e) => change('q', e.target.value)} />
          </label>
          <label>Your mood
            <select value={params.get('mood') || ''} onChange={(e) => change('mood', e.target.value)}>
              <option value="">Any mood</option>
              {moods.map((x) => <option key={x}>{x}</option>)}
            </select>
          </label>
          <label>Category
            <select value={params.get('category') || ''} onChange={(e) => change('category', e.target.value)}>
              <option value="">All categories</option>
              {categories.map((x) => <option key={x}>{x}</option>)}
            </select>
          </label>
          <label>Budget
            <select value={params.get('maxBudget') || ''} onChange={(e) => change('maxBudget', e.target.value)}>
              <option value="">Any budget</option>
              <option value="0">Free only</option><option value="1">$ · Budget friendly</option>
              <option value="2">$$ · A little extra</option><option value="3">$$$ · Treat yourself</option>
            </select>
          </label>
          <label>Time available
            <select value={params.get('maxDuration') || ''} onChange={(e) => change('maxDuration', e.target.value)}>
              <option value="">Any duration</option><option value="60">Up to 1 hour</option>
              <option value="120">Up to 2 hours</option><option value="180">Up to 3 hours</option>
            </select>
          </label>
          <label>Neighborhood
            <select value={params.get('neighborhood') || ''} onChange={(e) => change('neighborhood', e.target.value)}>
              <option value="">Anywhere in Chicago</option>
              {[...new Set(items.map((x) => x.neighborhood))].sort().map((x) => <option key={x}>{x}</option>)}
            </select>
          </label>
          <p className="filter-note">Place discovery uses Foursquare when configured, with OpenStreetMap as a no-key fallback. Event tickets open on Eventbrite.</p>
        </aside>

        <section className="results">
          <CatalogStatus />

          <div className="result-section-heading">
            <div><p className="eyebrow">LIVE DISCOVERY</p><h2>Cafés & places for your mood</h2></div>
            {placesLoading && <span className="muted">Finding Chicago spots…</span>}
          </div>
          {placesError && <div className="notice">Live places are temporarily unavailable. Curated recommendations are still below.</div>}
          {!placesLoading && !placesError && places.length === 0 && (
            <div className="notice">Live place results are unavailable right now. Curated Chicago recommendations are still available below.</div>
          )}
          <div className="experience-grid two-col live-grid">
            {places.map((item) => <PlaceCard key={item.id} item={item} />)}
          </div>

          <div className="eventbrite-panel">
            <div>
              <p className="eyebrow">EVENTS HAPPENING IN CHICAGO</p>
              <h2>Find tickets for your mood</h2>
              <p>Browse current Chicago events and complete ticket purchases on Eventbrite.</p>
            </div>
            <a className="primary" href={eventbriteUrl} target="_blank" rel="noreferrer">Explore Eventbrite tickets ↗</a>
          </div>

          <div className="result-section-heading curated-heading">
            <div><p className="eyebrow">CURATED CHICAGO</p><h2>More ideas to explore</h2></div>
            <p role="status"><strong>{filtered.length}</strong> matches</p>
          </div>
          <div className="experience-grid two-col">
            {filtered.map((item) => <ExperienceCard key={item.id} item={item} />)}
          </div>
          {!loading && !filtered.length && (
            <div className="empty-state">
              <span>✧</span><h2>A different kind of adventure?</h2>
              <p>No curated experiences match these filters. Try widening your search.</p>
              <button className="primary" onClick={() => setParams({})}>Clear filters</button>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
