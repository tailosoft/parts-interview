import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe } from 'app/shared/date/duration.pipe';
import { ISupplier } from '../supplier.model';

@Component({
  standalone: true,
  selector: 'jhi-supplier-detail',
  templateUrl: './supplier-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe],
})
export class SupplierDetailComponent {
  @Input() supplier: ISupplier | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
