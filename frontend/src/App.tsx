import { useEffect } from 'react';
import { Link, Routes, Route, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/HomePage';
import Explore from './pages/ExplorePage';
import Favorites from './pages/FavoritesPage';
import Detail from './pages/DetailPage';
import { FavoritesProvider, useFavorites } from './context/FavoritesContext';
import { CatalogProvider } from './context/CatalogContext';
function Shell() {
  const { storageError } = useFavorites();
  const { pathname } = useLocation();
  useEffect(() => {
    window.scrollTo(0, 0);
    document.title = `${pathname === '/explore' ? 'Explore' : pathname === '/favorites' ? 'Saved experiences' : 'Discover Chicago'} · Smart City Explorer`;
  }, [pathname]);
  return (
    <>
      <Navbar />
      {storageError && (
        <div className="notice" role="alert">
          Your browser could not save favorites. They will remain available for this session.
        </div>
      )}
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/explore" element={<Explore />} />
        <Route path="/favorites" element={<Favorites />} />
        <Route path="/experiences/:id" element={<Detail />} />
        <Route
          path="*"
          element={
            <main id="main" className="empty-state">
              <h1>Let’s get you back to the city.</h1>
              <Link className="primary" to="/">
                Go home
              </Link>
            </main>
          }
        />
      </Routes>
      <footer>
        <div className="footer-brand">✦ Smart City Explorer</div>
        <p>Made for the moments between the plans.</p>
        <small>Chicago sample collection · No live pricing or availability</small>
      </footer>
    </>
  );
}
export default function App() {
  return (
    <FavoritesProvider>
      <CatalogProvider>
        <Shell />
      </CatalogProvider>
    </FavoritesProvider>
  );
}
