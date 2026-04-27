export interface EventGroup {
  id: number;
  name: string;
}

export interface EventInfo {
  id: number;
  organizerEmail: string;
  organizerName: string;
  title: string;
  startDatetime: Date;
  location: string;
  description: string;
}

export interface EventResult {
  hits: EventInfo[];
  total_hits: 0;
  page: 0;
  size: 10;
}
