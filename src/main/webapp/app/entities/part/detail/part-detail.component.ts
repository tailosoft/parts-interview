import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe } from 'app/shared/date/duration.pipe';
import { IPart } from '../part.model';

@Component({
  standalone: true,
  selector: 'jhi-part-detail',
  templateUrl: './part-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe],
})
export class PartDetailComponent {
  @Input() part: IPart | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
