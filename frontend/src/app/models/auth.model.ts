export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone?: string;
  role?: string;
}

export interface AuthResponse {
  token: string;
  userId: number;
  fullName: string;
  role: string;
  email?: string;
}