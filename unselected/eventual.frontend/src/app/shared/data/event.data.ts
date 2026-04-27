export interface EventGroup {
  id: string;
  name: string;
  description: string;
  owner_email: string;
  is_public: boolean;
  member_count: number;
}

export interface EventInfo {
  id: number;
  event_id: number;
  group_id: number;
  organizer_email: string;
  organizer_name: string;
  title: string;
  start_date_time: Date;
  location: string;
  description: string;
  rsvps?: RSVPInfo[];
}

export interface EventResult {
  hits: EventInfo[];
  total_hits: 0;
  page: 0;
  size: 10;
}

export interface GroupResult {
  hits: EventGroup[];
  total_hits: 0;
  page: 0;
  size: 10;
}

export interface RSVPInfo {
  id: number;
  event_id: number;
  user_email: string;
  status: string;
}
