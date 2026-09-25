import { useCatalog } from '../context/CatalogContext';
export default function CatalogStatus() {
  const { demo, retry, loading } = useCatalog();
  if (loading)
    return (
      <div className="notice" role="status">
        Finding your next city moment…
      </div>
    );
  return demo ? (
    <div className="notice" role="status">
      <span>
        <strong>Demo mode.</strong> The API is unavailable. You’re browsing the bundled sample
        collection.
      </span>
      <button className="text-button" onClick={retry}>
        Retry connection
      </button>
    </div>
  ) : null;
}
