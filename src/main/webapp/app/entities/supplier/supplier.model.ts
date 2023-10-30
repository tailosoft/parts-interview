export interface ISupplier {
  id: number;
  name?: string | null;
}

export function getSupplierIdentifier(supplier: ISupplier): number {
  return supplier.id;
}
