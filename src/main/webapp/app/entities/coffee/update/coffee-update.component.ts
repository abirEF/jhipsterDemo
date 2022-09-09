import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { CoffeeFormService, CoffeeFormGroup } from './coffee-form.service';
import { ICoffee } from '../coffee.model';
import { CoffeeService } from '../service/coffee.service';

@Component({
  selector: 'jhi-coffee-update',
  templateUrl: './coffee-update.component.html',
})
export class CoffeeUpdateComponent implements OnInit {
  isSaving = false;
  coffee: ICoffee | null = null;

  editForm: CoffeeFormGroup = this.coffeeFormService.createCoffeeFormGroup();

  constructor(
    protected coffeeService: CoffeeService,
    protected coffeeFormService: CoffeeFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coffee }) => {
      this.coffee = coffee;
      if (coffee) {
        this.updateForm(coffee);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const coffee = this.coffeeFormService.getCoffee(this.editForm);
    if (coffee.id !== null) {
      this.subscribeToSaveResponse(this.coffeeService.update(coffee));
    } else {
      this.subscribeToSaveResponse(this.coffeeService.create(coffee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICoffee>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(coffee: ICoffee): void {
    this.coffee = coffee;
    this.coffeeFormService.resetForm(this.editForm, coffee);
  }
}
