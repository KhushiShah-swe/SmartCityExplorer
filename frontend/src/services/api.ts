import type { Experience } from '../types/Experience';
const base = (import.meta.env.VITE_API_URL || '/api').replace(/\/$/, '');
export async function getExperiences(signal: AbortSignal): Promise<Experience[]> {
  const response = await fetch(`${base}/experiences`, { signal });
  if (!response.ok) throw new Error(`API returned ${response.status}`);
  const body: unknown = await response.json();
  if (
    !Array.isArray(body) ||
    !body.every(
      (x) =>
        x &&
        Number.isSafeInteger(x.id) &&
        typeof x.name === 'string' &&
        typeof x.description === 'string' &&
        typeof x.category === 'string' &&
        typeof x.mood === 'string' &&
        typeof x.neighborhood === 'string' &&
        Number.isInteger(x.budgetLevel) &&
        Number.isInteger(x.durationMinutes) &&
        typeof x.hiddenGem === 'boolean',
    )
  )
    throw new Error('Unexpected API response');
  return body as Experience[];
}
