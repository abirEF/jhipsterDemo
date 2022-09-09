import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 55962,
};

export const sampleWithPartialData: IProject = {
  id: 56052,
  name: 'dedicated Agent deposit',
  code: 'users',
};

export const sampleWithFullData: IProject = {
  id: 24713,
  name: 'Borders Health',
  code: 'Respo',
};

export const sampleWithNewData: NewProject = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
