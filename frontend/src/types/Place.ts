export type Place = {
  id: string;
  name: string;
  address: string | null;
  category: string;
  mood: string;
  budgetLevel: number;
  rating: number | null;
  reviewCount: number | null;
  mapsUrl: string | null;
  directionsUrl: string | null;
  reviewsUrl: string | null;
  websiteUrl: string | null;
  openNow: boolean;
};
