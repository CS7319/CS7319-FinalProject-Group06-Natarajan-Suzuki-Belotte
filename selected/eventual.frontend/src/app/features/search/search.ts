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
      this.getSocialEvents(params['group']);
    });
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

  getSocialGroups(): void {
    this.searchService.getGroups().subscribe({
      error: () => {
        this.socialGroups.set([
          { id: 1, name: 'Group 1' },
          { id: 2, name: 'Group 2' },
          { id: 3, name: 'Group 3' },
          { id: 4, name: 'Group 4' },
        ]);
      },
      next: (groups) => this.socialGroups.set(groups || []),
    });
  }
}
