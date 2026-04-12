import { Component, inject } from '@angular/core';
import { SearchService } from './search.service';
import { SocialEvent } from '../../shared/data/social-event.data';

@Component({
  selector: 'app-search',
  imports: [],
  templateUrl: './search.html',
  styleUrl: './search.scss',
})
export class Search {
  socialEvents: SocialEvent[] = [];

  private readonly searchService = inject(SearchService);

  getSocialEvents(query: string): void {
    this.searchService.get(query).subscribe((events) => {
      this.socialEvents = events || [];
    });
  }
}
