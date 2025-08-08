export interface GoogleAuthResponse {
  access_token: string;
  refresh_token: string;
  expires_in: number;
  user: {
    email: string;
    name: string;
    role: string;
    profileImageUrl: string;
  };
}
