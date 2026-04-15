export interface EventInfo {
  id: number;
  host: EventHost;
  name: string;
  date: string;
  location: string;
  description: string;
}

export interface EventGroup {
  id: number;
  host: EventHost;
  name: string;
  description: string;
}

export interface EventHost {
  handle: string;
  name: string;
}
