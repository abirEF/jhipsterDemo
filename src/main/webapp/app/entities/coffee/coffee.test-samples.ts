import { ICoffee, NewCoffee } from './coffee.model';

export const sampleWithRequiredData: ICoffee = {
  id: 83767,
  name: 'Tunisia en',
};

export const sampleWithPartialData: ICoffee = {
  id: 61679,
  fieldName: 'Quetzal Cambridgeshire',
  name: 'UIC-Franc ',
};

export const sampleWithFullData: ICoffee = {
  id: 44436,
  fieldName: 'neural utilize',
  name: 'Sports pix',
};

export const sampleWithNewData: NewCoffee = {
  name: 'bus',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
