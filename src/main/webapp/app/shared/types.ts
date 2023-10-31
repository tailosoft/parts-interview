import { FormGroup } from '@angular/forms';

export type PartialNullable<T> = { [K in keyof T]?: T[K] | null };

export type FormGroupType<Type> = Type extends FormGroup<infer X> ? X : never;
