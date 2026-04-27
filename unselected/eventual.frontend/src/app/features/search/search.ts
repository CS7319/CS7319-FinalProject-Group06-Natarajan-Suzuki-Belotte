import { Component, inject, OnInit, signal } from '@angular/core';
import { SearchService } from './search.service';
import { EventInfo, EventGroup } from '../../shared/data/event.data';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-search',
  imports: [RouterLink],
  templateUrl: './search.html',
  styleUrl: './search.scss',
})
export class Search implements OnInit {
  socialEvents = signal<EventInfo[]>([]);
  socialGroups = signal<EventGroup[]>([]);

  private readonly searchService = inject(SearchService);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.getSocialGroups();

    this.route.queryParams.subscribe((params) => {
      this.getSocialEvents(params['query'] || '');
    });
  }

  getSocialEvents(query: string): void {
    this.searchService.getEvents(query).subscribe({
      error: (error) => {
        console.error('Error fetching events', error);
        this.socialEvents.set([
          {
            id: 1,
            event_id: 1,
            group_id: 1,
            organizer_email: 'Peruna',
            organizer_name: 'hello@smu.edu',
            title: 'Event 1',
            start_date_time: new Date(),
            location: 'Dallas, TX',
            description:
              'Lorem ipsum dolor sit amet consectetur adipisicing elit. Magni ratione recusandae voluptas totam aut dicta, aspernatur velit molestiae obcaecati pariatur quas accusamus dolorum iusto blanditiis nihil, saepe a at. Nihil!',
          },
          {
            id: 2,
            event_id: 2,
            group_id: 2,
            organizer_email: 'Peruna',
            organizer_name: 'hello@smu.edu',
            title: 'Event 2',
            start_date_time: new Date(),
            location: 'Fort Worth, TX',
            description:
              'Lorem ipsum dolor sit amet consectetur adipisicing elit. Magni ratione recusandae voluptas totam aut dicta, aspernatur velit molestiae obcaecati pariatur quas accusamus dolorum iusto blanditiis nihil, saepe a at. Nihil!',
          },
        ]);
      },
      next: (events) => {
        this.socialEvents.set(events?.hits || []);
      },
    });
  }

  getSocialGroups(): void {
    this.searchService.getGroups().subscribe({
      error: () => {
        this.socialGroups.set([
          {
            id: '10',
            name: 'Group 10 - Gaming',
            description: 'Auto-generated group #10',
            owner_email: 'jade.ward7@example.com',
            is_public: true,
            member_count: 1,
          },
          {
            id: '1',
            name: 'Group 1 - Travel',
            description: 'Auto-generated group #1',
            owner_email: 'jade.ward7@example.com',
            is_public: true,
            member_count: 1,
          },
        ]);
      },
      next: (group) => this.socialGroups.set(group?.hits || []),
    });
  }
}
