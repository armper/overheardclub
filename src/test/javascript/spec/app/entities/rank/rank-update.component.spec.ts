import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { RankUpdateComponent } from 'app/entities/rank/rank-update.component';
import { RankService } from 'app/entities/rank/rank.service';
import { Rank } from 'app/shared/model/rank.model';

describe('Component Tests', () => {
  describe('Rank Management Update Component', () => {
    let comp: RankUpdateComponent;
    let fixture: ComponentFixture<RankUpdateComponent>;
    let service: RankService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [RankUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(RankUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RankUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RankService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Rank(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Rank();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
