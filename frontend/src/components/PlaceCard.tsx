import type { Place } from '../types/Place';
import { budgetLabel } from '../lib/discovery';

export default function PlaceCard({ item }: { item: Place }) {
  return (
    <article className="experience-card live-place-card">
      <div className="card-art art-cafés">
        <span className="art-ring" />
        <span className="art-symbol" aria-hidden="true">⌖</span>
        <span className="category-label">{item.category}</span>
        <span className="gem">LIVE · CHICAGO</span>
      </div>
      <div className="card-content">
        <p className="location">{item.address || 'Chicago, IL'}</p>
        <h3>{item.name}</h3>
        <div className="place-meta">
          {item.rating != null && <strong>★ {item.rating.toFixed(1)}</strong>}
          {item.reviewCount != null && <span>{item.reviewCount.toLocaleString()} ratings</span>}
          <span>{budgetLabel(item.budgetLevel)}</span>
          {item.openNow && <span className="open-now">Open now</span>}
        </div>
        <p className="card-description">
          A live Chicago recommendation matched to <strong>{item.mood}</strong>.
        </p>
        <div className="card-actions">
          {item.mapsUrl && <a className="primary compact" href={item.mapsUrl} target="_blank" rel="noreferrer">Open in Maps</a>}
          {item.reviewsUrl && <a className="text-link" href={item.reviewsUrl} target="_blank" rel="noreferrer">Reviews</a>}
          {item.directionsUrl && <a className="text-link" href={item.directionsUrl} target="_blank" rel="noreferrer">Directions</a>}
        </div>
      </div>
    </article>
  );
}
