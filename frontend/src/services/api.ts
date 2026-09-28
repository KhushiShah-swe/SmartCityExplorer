import type { Experience } from '../types/Experience';
import type { Place } from '../types/Place';

const base = (import.meta.env.VITE_API_URL || '/api').replace(/\/$/, '');

export async function getExperiences(signal: AbortSignal): Promise<Experience[]> {
  const response = await fetch(`${base}/experiences`, { signal });
  if (!response.ok) throw new Error(`API returned ${response.status}`);
  const body: unknown = await response.json();
  if (!Array.isArray(body)) throw new Error('Unexpected API response');
  return body as Experience[];
}

export async function getLivePlaces(
  signal: AbortSignal,
  mood?: string,
  maxBudget?: string,
): Promise<Place[]> {
  const params = new URLSearchParams();
  if (mood) params.set('mood', mood);
  if (maxBudget) params.set('maxBudget', maxBudget);
  const response = await fetch(`${base}/discover/places?${params}`, { signal });
  if (!response.ok) throw new Error(`Places API returned ${response.status}`);
  const body: unknown = await response.json();
  if (!Array.isArray(body)) throw new Error('Unexpected Places response');
  return body as Place[];
}
