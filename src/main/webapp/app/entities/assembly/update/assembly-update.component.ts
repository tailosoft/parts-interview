import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { finalize, Observable, ReplaySubject } from 'rxjs';
import { LazySubscriber } from 'app/core/request/lazy-subscriber';
import { DropdownLazyLoadEvent } from 'primeng/dropdown';

import { AssemblyFormService, AssemblyFormGroup } from './assembly-form.service';
import { IAssembly } from '../assembly.model';
import { AssemblyService } from '../service/assembly.service';
import { MessageService } from 'primeng/api';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';

@Component({
  standalone: true,
  selector: 'jhi-assembly-update',
  templateUrl: './assembly-update.component.html',
  imports: [SharedModule, ReactiveFormsModule],
})
export class AssemblyUpdateComponent implements OnInit, OnDestroy {
  edit = false;
  isSaving = false;
  parentOptionsSubscriber?: LazySubscriber<IPart>;
  parentSelectedOptions: IPart[] | null = null;
  parentFilterValue?: any;
  childOptionsSubscriber?: LazySubscriber<IPart>;
  childSelectedOptions: IPart[] | null = null;
  childFilterValue?: any;

  editForm: AssemblyFormGroup = this.assemblyFormService.createAssemblyFormGroup();

  private onDestroySubject = new ReplaySubject(1);

  constructor(
    protected messageService: MessageService,
    protected assemblyService: AssemblyService,
    protected assemblyFormService: AssemblyFormService,
    protected partService: PartService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.isSaving = false;

    this.parentOptionsSubscriber = new LazySubscriber(req => this.partService.query(req), this.onDestroySubject);
    this.childOptionsSubscriber = new LazySubscriber(req => this.partService.query(req), this.onDestroySubject);

    this.activatedRoute.data.subscribe(({ assembly }) => {
      this.updateForm(assembly);
    });
  }

  ngOnDestroy(): void {
    this.onDestroySubject.next(undefined);
    this.onDestroySubject.complete();
  }

  onParentLazyLoadEvent(event: Partial<DropdownLazyLoadEvent>): void {
    this.parentOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  onChildLazyLoadEvent(event: Partial<DropdownLazyLoadEvent>): void {
    this.childOptionsSubscriber!.filter({ first: event.first, last: event.last, 'name.contains': event.filter });
  }

  updateForm(assembly: IAssembly | null): void {
    if (assembly) {
      this.edit = true;
      this.editForm.reset({ ...assembly }, { emitEvent: false, onlySelf: true });
      this.parentOptionsSubscriber!.options = [assembly.parent];
      this.parentFilterValue = assembly.parent.name;
      this.childOptionsSubscriber!.options = [assembly.child];
      this.childFilterValue = assembly.child.name;
    } else {
      this.edit = false;
      this.editForm.reset({});
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    if (this.editForm.valid) {
      this.isSaving = true;
      const assembly = this.editForm.value;
      if (this.edit) {
        this.subscribeToSaveResponse(this.assemblyService.update(assembly as IAssembly));
      } else {
        this.subscribeToSaveResponse(this.assemblyService.create(assembly as IAssembly));
      }
    } else {
      this.editForm.markAllAsTouched();
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAssembly>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
}
