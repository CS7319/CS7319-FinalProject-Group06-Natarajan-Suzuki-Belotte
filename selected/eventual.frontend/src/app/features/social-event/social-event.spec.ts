import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialEvent } from './social-event';

describe('SocialEvent', () => {
  let component: SocialEvent;
  let fixture: ComponentFixture<SocialEvent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SocialEvent],
    }).compileComponents();

    fixture = TestBed.createComponent(SocialEvent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
