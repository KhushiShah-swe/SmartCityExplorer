import { Link } from 'react-router-dom';
import type { Experience } from '../types/Experience';
import { useFavorites } from '../context/FavoritesContext';
import { budgetLabel } from '../lib/discovery';
const symbols: Record<string, string> = {
  Outdoors: '↟',
  Culture: '▥',
  Cafés: '☕',
  Dining: '◒',
  Activities: '✦',
  Nightlife: '☾',
};
export default function ExperienceCard({ item }: { item: Experience }) {
  const { ids, toggle } = useFavorites();
  const saved = ids.includes(item.id);
  return (
    <article className="experience-card">
      <div className={`card-art art-${item.category.toLowerCase()}`}>
        <span className="art-ring" />
        <span className="art-symbol" aria-hidden="true">
          {symbols[item.category] || '✦'}
        </span>
        <span className="category-label">{item.category}</span>
        <button
          className="save-button"
          aria-label={`${saved ? 'Unsave' : 'Save'} ${item.name}`}
          aria-pressed={saved}
          onClick={() => toggle(item.id)}
        >
          {saved ? '♥' : '♡'}
        </button>
        {item.hiddenGem && <span className="gem">LOCAL DISCOVERY</span>}
      </div>
      <div className="card-content">
        <p className="location">
          {item.neighborhood} <span>· {item.durationMinutes} min</span>
        </p>
        <h3>
          <Link to={`/experiences/${item.id}`}>{item.name}</Link>
        </h3>
        <p className="card-description">{item.description}</p>
        <div className="card-bottom">
          <span className="mood-tag">{item.mood}</span>
          <strong>{budgetLabel(item.budgetLevel)}</strong>
        </div>
      </div>
    </article>
  );
}
