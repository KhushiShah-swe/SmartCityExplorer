import { Link } from 'react-router-dom';
import { moods } from '../lib/discovery';
import Scene from '../components/Scene';
import ExperienceCard from '../components/ExperienceCard';
import CatalogStatus from '../components/CatalogStatus';
import { useCatalog } from '../context/CatalogContext';
export default function HomePage() {
  const { items } = useCatalog();
  return (
    <main id="main">
      <section className="hero">
        <div className="hero-copy">
          <p className="eyebrow">
            <span /> YOUR CITY. YOUR KIND OF DAY.
          </p>
          <h1>
            Less scrolling.
            <br />
            More <em>Chicago.</em>
          </h1>
          <p>
            Big plans or a little escape. Find a city experience that fits your mood, your time, and
            your budget.
          </p>
          <div className="actions">
            <Link className="primary" to="/explore">
              Find my next experience <span>↗</span>
            </Link>
            <Link className="text-link" to="/explore?maxBudget=0">
              Explore for free →
            </Link>
          </div>
          <div className="hero-note">
            <span className="tiny-stars">✶ ✶ ✶ ✶</span> A fresh perspective on the Windy City
          </div>
        </div>
        <div className="hero-visual">
          <Scene />
          <div className="visual-caption">
            <span>01 / THE CITY IS YOURS</span>
            <strong>Take the scenic route.</strong>
          </div>
          <div className="floating-note">
            <span>↗</span>
            <div>
              Your next good day
              <br />
              <strong>starts right here.</strong>
            </div>
          </div>
        </div>
      </section>
      <section className="mood-section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">START WITH A FEELING</p>
            <h2>What’s your kind of day?</h2>
          </div>
          <span className="muted">There’s a Chicago for every mood.</span>
        </div>
        <div className="mood-grid">
          {moods.map((mood, i) => (
            <Link key={mood} to={`/explore?mood=${encodeURIComponent(mood)}`} className="mood-card">
              <span aria-hidden="true">{['✧', '☁', '♡', '▤', '✦', '☾'][i]}</span>
              <strong>{mood}</strong>
              <span className="mood-arrow">↗</span>
            </Link>
          ))}
        </div>
      </section>
      <section>
        <div className="section-heading">
          <div>
            <p className="eyebrow">A LITTLE INSPIRATION</p>
            <h2>Make a day of it.</h2>
          </div>
          <Link className="text-link" to="/explore">
            Browse all experiences →
          </Link>
        </div>
        <CatalogStatus />
        <div className="experience-grid">
          {items.slice(0, 3).map((item) => (
            <ExperienceCard key={item.id} item={item} />
          ))}
        </div>
      </section>
      <section className="closing-banner">
        <div>
          <p className="eyebrow">LESS PLANNING. MORE LIVING.</p>
          <h2>
            Good days don’t need
            <br />
            big budgets.
          </h2>
        </div>
        <Link className="primary light" to="/explore?maxBudget=0">
          Find free experiences ↗
        </Link>
      </section>
    </main>
  );
}
