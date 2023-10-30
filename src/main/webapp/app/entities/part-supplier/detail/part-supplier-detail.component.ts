import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe } from 'app/shared/date/duration.pipe';
import { IPartSupplier } from '../part-supplier.model';

@Component({
  standalone: true,
  selector: 'jhi-part-supplier-detail',
  templateUrl: './part-supplier-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe],
})
export class PartSupplierDetailComponent {
  @Input() partSupplier: IPartSupplier | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
