import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe } from 'app/shared/date/duration.pipe';
import { IAssembly } from '../assembly.model';

@Component({
  standalone: true,
  selector: 'jhi-assembly-detail',
  templateUrl: './assembly-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe],
})
export class AssemblyDetailComponent {
  @Input() assembly: IAssembly | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
