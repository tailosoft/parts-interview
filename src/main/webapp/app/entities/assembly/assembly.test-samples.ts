import { IAssembly } from './assembly.model';

export const sampleWithRequiredData: IAssembly = {
  parent: {
    id: 18792,
  },
  child: {
    id: 24181,
  },
  quantity: 9802,
};

export const sampleWithPartialData: IAssembly = {
  parent: {
    id: 11561,
  },
  child: {
    id: 13589,
  },
  quantity: 24319,
} as any as IAssembly;

export const sampleWithFullData: IAssembly = {
  parent: {
    id: 2042,
  },
  child: {
    id: 5412,
  },
  quantity: 22544,
};

export const sampleWithNewData: IAssembly = {
  parent: {
    id: 27579,
  },
  child: {
    id: 26094,
  },
  quantity: 7470,
} as any as IAssembly;

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
