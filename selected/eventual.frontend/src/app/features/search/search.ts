import { Component, inject } from '@angular/core';
import { SearchService } from './search.service';
import { SocialEvent, SocialGroup } from '../../shared/data/event.data';

@Component({
  selector: 'app-search',
  imports: [],
  templateUrl: './search.html',
  styleUrl: './search.scss',
})
export class Search {
  socialEvents: SocialEvent[] = [];
  socialGroups: SocialGroup[] = [];

  private readonly searchService = inject(SearchService);

  getSocialEvents(query: string): void {
    this.searchService.getEvents(query).subscribe((events) => {
      this.socialEvents = events || [];
    });
  }

  getSocialGroups(query: string): void {
    this.searchService.getGroups(query).subscribe((groups) => {
      this.socialGroups = groups || [];
    });
  }
}
