export interface GoogleAuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: {
    userId: number;
    email: string;
    name: string;
    role: string;
    profileImageUrl: string;
  };
}
