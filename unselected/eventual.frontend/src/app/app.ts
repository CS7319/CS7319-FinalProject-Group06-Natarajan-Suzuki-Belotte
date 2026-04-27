import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PageFooter } from './shared/page-footer/page-footer';
import { PageHeader } from './shared/page-header/page-header';

@Component({
  selector: 'app-root',
  imports: [PageFooter, PageHeader, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('eventual.frontend');
}
