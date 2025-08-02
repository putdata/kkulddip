export interface GoogleAuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: {
    email: string;
    name: string;
    role: string;
    profileImageUrl: string;
  };
}
