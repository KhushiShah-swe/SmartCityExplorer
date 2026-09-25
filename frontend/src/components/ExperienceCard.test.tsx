// @vitest-environment jsdom
import { afterEach, beforeEach, expect, it } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { FavoritesProvider } from '../context/FavoritesContext';
import ExperienceCard from './ExperienceCard';
import samples from '../../../shared/experiences.json';
beforeEach(() => localStorage.clear());
afterEach(cleanup);
it('saves, announces the pressed state, persists, and removes an experience', () => {
  render(
    <MemoryRouter>
      <FavoritesProvider>
        <ExperienceCard item={samples[0]} />
      </FavoritesProvider>
    </MemoryRouter>,
  );
  fireEvent.click(screen.getByRole('button', { name: 'Save Chicago Riverwalk' }));
  const saved = screen.getByRole('button', { name: 'Unsave Chicago Riverwalk' });
  expect(saved.getAttribute('aria-pressed')).toBe('true');
  expect(JSON.parse(localStorage.getItem('sce:favorites:v1')!)).toEqual([1]);
  fireEvent.click(saved);
  expect(
    screen.getByRole('button', { name: 'Save Chicago Riverwalk' }).getAttribute('aria-pressed'),
  ).toBe('false');
  expect(JSON.parse(localStorage.getItem('sce:favorites:v1')!)).toEqual([]);
});
