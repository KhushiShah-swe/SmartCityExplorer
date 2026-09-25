import { describe, expect, it } from 'vitest';
import samples from '../../../shared/experiences.json';
import { filterExperiences, readFavoriteIds } from './discovery';
describe('experience discovery', () => {
  it('combines mood, budget, and time filters', () => {
    const found = filterExperiences(
      samples,
      new URLSearchParams('mood=Study+%26+Work&maxBudget=0&maxDuration=60'),
    );
    expect(found.map((x) => x.name)).toEqual(['Harold Washington Library']);
  });
  it('preserves a zero budget rather than treating it as an empty filter', () => {
    const found = filterExperiences(samples, new URLSearchParams('maxBudget=0'));
    expect(found.length).toBe(4);
    expect(found.every((x) => x.budgetLevel === 0)).toBe(true);
  });
  it('searches neighborhoods case-insensitively', () => {
    expect(filterExperiences(samples, new URLSearchParams('q=PILSEN'))[0].name).toBe(
      'Pilsen Mural Walk',
    );
  });
  it('composes category and neighborhood filters', () => {
    expect(
      filterExperiences(samples, new URLSearchParams('category=Culture&neighborhood=Loop')),
    ).toHaveLength(2);
  });
  it('sorts by time without mutating the original collection', () => {
    const first = samples[0].id;
    const found = filterExperiences(samples, new URLSearchParams('sort=duration'));
    expect(found[0].durationMinutes).toBe(60);
    expect(found.at(-1)?.durationMinutes).toBe(180);
    expect(samples[0].id).toBe(first);
  });
  it('returns an empty collection when nothing matches', () => {
    expect(filterExperiences(samples, new URLSearchParams('q=not-a-real-place'))).toEqual([]);
  });
});
describe('favorite storage', () => {
  it('recovers from corrupted JSON', () => {
    expect(readFavoriteIds({ getItem: () => '{bad' })).toEqual([]);
  });
  it('handles storage access failures', () => {
    expect(
      readFavoriteIds({
        getItem: () => {
          throw new Error('denied');
        },
      }),
    ).toEqual([]);
  });
  it('discards invalid ids and deduplicates', () => {
    expect(readFavoriteIds({ getItem: () => '[1,1,2,"3",-1,null,1.5]' })).toEqual([1, 2]);
  });
  it('rejects non-array data', () => {
    expect(readFavoriteIds({ getItem: () => '{"id":1}' })).toEqual([]);
  });
});
