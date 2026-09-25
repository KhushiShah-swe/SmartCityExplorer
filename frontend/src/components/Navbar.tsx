import { NavLink, Link } from 'react-router-dom';
import { useFavorites } from '../context/FavoritesContext';
export default function Navbar() {
  const { ids } = useFavorites();
  return (
    <header className="site-header">
      <a className="skip-link" href="#main">
        Skip to content
      </a>
      <nav aria-label="Main navigation">
        <Link className="brand" to="/">
          <span className="brand-mark" aria-hidden="true">
            ✦
          </span>
          <span>
            Smart City<span className="brand-sub">EXPLORER</span>
          </span>
        </Link>
        <div className="nav-links">
          <NavLink to="/" end>
            Home
          </NavLink>
          <NavLink to="/explore">Explore</NavLink>
          <NavLink to="/favorites">
            Saved <span className="count">{ids.length}</span>
          </NavLink>
        </div>
        <span className="city-label">
          CHICAGO, IL <span aria-hidden="true">✶</span>
        </span>
      </nav>
    </header>
  );
}
