import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IPart {
  id: number;
  name?: string | null;
  assemblyCost?: number | null;
  bestPrice?: number | null;
  bestSupplier?: Pick<ISupplier, 'id' | 'name'> | null;
}

export function getPartIdentifier(part: IPart): number {
  return part.id;
}
