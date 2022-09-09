export interface ICoffee {
  id: number;
  fieldName?: string | null;
  name?: string | null;
}

export type NewCoffee = Omit<ICoffee, 'id'> & { id: null };
