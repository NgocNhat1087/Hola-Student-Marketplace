export type User = {
  id: number;
  email: string;
  role: string;
  verified: boolean;
};

export type Profile = {
  userId: number;
  nickname: string;
  avatarUrl?: string | null;
  coverPhotoUrl?: string | null;
  bio?: string | null;
  averageRating?: number | null;
  ratingCount?: number | null;
  sellerRank?: number | null;
};

export type ApiError = {
  error?: string;
  message?: string;
};
