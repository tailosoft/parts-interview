import { IPart } from 'app/entities/part/part.model';

export interface IAssembly {
  quantity?: number | null;
  parent: Pick<IPart, 'id' | 'name'>;
  child: Pick<IPart, 'id' | 'name'>;
}

export function getAssemblyIdentifier(assembly: IAssembly): string {
  return `parentId=${assembly.parent.id};childId=${assembly.child.id}`;
}
