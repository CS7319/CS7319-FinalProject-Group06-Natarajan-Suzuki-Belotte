export interface EventGroup {
  id: number;
  host: EventHost;
  name: string;
  description: string;
}

export interface EventInfo {
  id: number;
  hosts: EventHost[];
  name: string;
  date: Date;
  location: string;
  description: string;
}

export interface EventHost {
  email: string;
  name: string;
}
