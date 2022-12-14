import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICoffee } from '../coffee.model';
import { CoffeeService } from '../service/coffee.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './coffee-delete-dialog.component.html',
})
export class CoffeeDeleteDialogComponent {
  coffee?: ICoffee;

  constructor(protected coffeeService: CoffeeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.coffeeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
