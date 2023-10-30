import { ISupplier } from './supplier.model';

export const sampleWithRequiredData: ISupplier = {
  id: 7783,
  name: 'Bespoke North',
};

export const sampleWithPartialData: ISupplier = {
  id: 24986,
  name: 'West Bromine Diverse',
} as any as ISupplier;

export const sampleWithFullData: ISupplier = {
  id: 32126,
  name: 'Executive diesel',
};

export const sampleWithNewData: ISupplier = {
  name: 'gold Buckinghamshire Configuration',
} as any as ISupplier;

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
