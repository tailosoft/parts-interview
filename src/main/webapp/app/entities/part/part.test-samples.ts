import { IPart } from './part.model';

export const sampleWithRequiredData: IPart = {
  id: 29795,
  name: 'Maine Product',
};

export const sampleWithPartialData: IPart = {
  id: 12149,
  name: 'maximize Plastic',
} as any as IPart;

export const sampleWithFullData: IPart = {
  id: 23062,
  name: 'Assistant copy Buckinghamshire',
  assemblyCost: 1129,
  bestPrice: 7086,
};

export const sampleWithNewData: IPart = {
  name: 'per pigeon',
} as any as IPart;

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
