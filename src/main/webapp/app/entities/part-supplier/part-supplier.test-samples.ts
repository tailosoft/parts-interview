import { IPartSupplier } from './part-supplier.model';

export const sampleWithRequiredData: IPartSupplier = {
  part: {
    id: 30014,
  },
  supplier: {
    id: 10245,
  },
  price: 6235,
};

export const sampleWithPartialData: IPartSupplier = {
  part: {
    id: 206,
  },
  supplier: {
    id: 14029,
  },
  price: 4819,
} as any as IPartSupplier;

export const sampleWithFullData: IPartSupplier = {
  part: {
    id: 7260,
  },
  supplier: {
    id: 10526,
  },
  price: 5548,
};

export const sampleWithNewData: IPartSupplier = {
  part: {
    id: 26873,
  },
  supplier: {
    id: 22603,
  },
  price: 22968,
} as any as IPartSupplier;

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
