import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CoffeeFormService } from './coffee-form.service';
import { CoffeeService } from '../service/coffee.service';
import { ICoffee } from '../coffee.model';

import { CoffeeUpdateComponent } from './coffee-update.component';

describe('Coffee Management Update Component', () => {
  let comp: CoffeeUpdateComponent;
  let fixture: ComponentFixture<CoffeeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let coffeeFormService: CoffeeFormService;
  let coffeeService: CoffeeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CoffeeUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CoffeeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoffeeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    coffeeFormService = TestBed.inject(CoffeeFormService);
    coffeeService = TestBed.inject(CoffeeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const coffee: ICoffee = { id: 456 };

      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      expect(comp.coffee).toEqual(coffee);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoffee>>();
      const coffee = { id: 123 };
      jest.spyOn(coffeeFormService, 'getCoffee').mockReturnValue(coffee);
      jest.spyOn(coffeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coffee }));
      saveSubject.complete();

      // THEN
      expect(coffeeFormService.getCoffee).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(coffeeService.update).toHaveBeenCalledWith(expect.objectContaining(coffee));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoffee>>();
      const coffee = { id: 123 };
      jest.spyOn(coffeeFormService, 'getCoffee').mockReturnValue({ id: null });
      jest.spyOn(coffeeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coffee: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coffee }));
      saveSubject.complete();

      // THEN
      expect(coffeeFormService.getCoffee).toHaveBeenCalled();
      expect(coffeeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoffee>>();
      const coffee = { id: 123 };
      jest.spyOn(coffeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coffee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(coffeeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
