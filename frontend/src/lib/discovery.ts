import type { Experience } from '../types/Experience';
export const moods = [
  'Explore Chicago',
  'Slow & Relaxed',
  'Date Night',
  'Study & Work',
  'Group Activities',
  'Party & Nightlife',
];
export const categories = ['Outdoors', 'Culture', 'Cafés', 'Dining', 'Activities', 'Nightlife'];
export const budgetLabel = (level: number) => (level === 0 ? 'Free' : '$'.repeat(level));
export function filterExperiences(items: Experience[], params: URLSearchParams): Experience[] {
  const q = (params.get('q') || '').trim().toLowerCase();
  const filtered = items.filter(
    (x) =>
      (!q || `${x.name} ${x.description} ${x.neighborhood}`.toLowerCase().includes(q)) &&
      (!params.get('mood') || x.mood === params.get('mood')) &&
      (!params.get('category') || x.category === params.get('category')) &&
      (!params.get('neighborhood') || x.neighborhood === params.get('neighborhood')) &&
      (!params.has('maxBudget') || x.budgetLevel <= Number(params.get('maxBudget'))) &&
      (!params.has('maxDuration') || x.durationMinutes <= Number(params.get('maxDuration'))),
  );
  return [...filtered].sort((a, b) =>
    params.get('sort') === 'budget'
      ? a.budgetLevel - b.budgetLevel || a.name.localeCompare(b.name)
      : params.get('sort') === 'duration'
        ? a.durationMinutes - b.durationMinutes || a.name.localeCompare(b.name)
        : a.name.localeCompare(b.name),
  );
}
export function readFavoriteIds(storage: Pick<Storage, 'getItem'>): number[] {
  try {
    const value: unknown = JSON.parse(storage.getItem('sce:favorites:v1') || '[]');
    return Array.isArray(value)
      ? [
          ...new Set(
            value.filter(
              (id): id is number => typeof id === 'number' && Number.isSafeInteger(id) && id > 0,
            ),
          ),
        ]
      : [];
  } catch {
    return [];
  }
}
