import { Component, inject, OnInit, signal } from '@angular/core';
import { SearchService } from './search.service';
import { EventInfo, EventGroup } from '../../shared/data/event.data';

@Component({
  selector: 'app-search',
  imports: [],
  templateUrl: './search.html',
  styleUrl: './search.scss',
})
export class Search implements OnInit {
  socialEvents = signal<EventInfo[]>([]);
  socialGroups = signal<EventGroup[]>([]);

  private readonly searchService = inject(SearchService);

  ngOnInit(): void {
    this.getSocialEvents('');
  }

  getSocialEvents(query: string): void {
    this.searchService.getEvents(query).subscribe({
      error: () => {
        this.socialEvents.set([
          {
            id: 1,
            hosts: [{ email: 'hello@smu.edu', name: 'Peruna' }],
            name: 'Event 1',
            date: new Date(),
            location: 'Dallas, TX',
            description:
              'Lorem ipsum dolor sit amet consectetur adipisicing elit. Magni ratione recusandae voluptas totam aut dicta, aspernatur velit molestiae obcaecati pariatur quas accusamus dolorum iusto blanditiis nihil, saepe a at. Nihil!',
          },
          {
            id: 2,
            hosts: [{ email: 'hello@smu.edu', name: 'Peruna' }],
            name: 'Event 2',
            date: new Date(),
            location: 'Fort Worth, TX',
            description:
              'Lorem ipsum dolor sit amet consectetur adipisicing elit. Magni ratione recusandae voluptas totam aut dicta, aspernatur velit molestiae obcaecati pariatur quas accusamus dolorum iusto blanditiis nihil, saepe a at. Nihil!',
          },
          {
            id: 3,
            hosts: [{ email: 'hello@smu.edu', name: 'Peruna' }],
            name: 'Event 3',
            date: new Date(),
            location: 'Austin, TX',
            description:
              'Lorem ipsum dolor sit amet consectetur adipisicing elit. Magni ratione recusandae voluptas totam aut dicta, aspernatur velit molestiae obcaecati pariatur quas accusamus dolorum iusto blanditiis nihil, saepe a at. Nihil!',
          },
        ]);
      },
      next: (events) => this.socialEvents.set(events || []),
    });
  }

  getSocialGroups(query: string): void {
    this.searchService.getGroups(query).subscribe({
      error: () => {},
      next: (groups) => this.socialGroups.set(groups || []),
    });
  }
}
