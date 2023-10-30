import { IPart } from 'app/entities/part/part.model';
import { ISupplier } from 'app/entities/supplier/supplier.model';

export interface IPartSupplier {
  price?: number | null;
  part: Pick<IPart, 'id' | 'name'>;
  supplier: Pick<ISupplier, 'id' | 'name'>;
}

export function getPartSupplierIdentifier(partSupplier: IPartSupplier): string {
  return `partId=${partSupplier.part.id};supplierId=${partSupplier.supplier.id}`;
}
