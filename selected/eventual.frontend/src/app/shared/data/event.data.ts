export interface SocialEvent {
  id: number;
  host: SocialHost;
  name: string;
  date: string;
  location: string;
  description: string;
}

export interface SocialGroup {
  id: number;
  host: SocialHost;
  name: string;
  description: string;
}

export interface SocialHost {
  handle: string;
  name: string;
}
