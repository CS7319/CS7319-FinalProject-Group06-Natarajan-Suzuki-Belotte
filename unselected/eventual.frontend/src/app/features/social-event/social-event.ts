import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SocialEventService } from './social-event.service';
import { EventInfo } from '../../shared/data/event.data';

@Component({
  selector: 'app-social-event',
  imports: [],
  templateUrl: './social-event.html',
  styleUrl: './social-event.scss',
})
export class SocialEvent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly socialEventService = inject(SocialEventService);

  socialEvent = signal<EventInfo>({} as EventInfo);

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.getSocialEvent(params['id'] || '');
    });
  }

  getSocialEvent(query: string) {
    this.socialEventService.get(query).subscribe({
      error: () => {
        this.socialEvent.set({
          id: 2026,
          event_id: 2026,
          group_id: 1,
          organizer_name: 'Peruna',
          organizer_email: 'hello@smu.edu',
          title: 'Event 2026',
          start_date_time: new Date(),
          location: 'Dallas, TX',
          description:
            'Lorem ipsum dolor sit amet consectetur adipisicing elit. Magni ratione recusandae voluptas totam aut dicta, aspernatur velit molestiae obcaecati pariatur quas accusamus dolorum iusto blanditiis nihil, saepe a at. Nihil!',
        });
      },
      next: (event) => {
        console.log('Fetched event', event);
        this.socialEventService.getRSVPs(event?.event_id).subscribe({
          error: (error) => console.error('Error fetching RSVPs', error),
          next: (rsvps) => {
            this.socialEvent.set(event);
            this.socialEvent().rsvps = rsvps.slice(0, 3);
          },
        });
      },
    });
  }
}
