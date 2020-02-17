import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { RankingUpdateComponent } from 'app/entities/ranking/ranking-update.component';
import { RankingService } from 'app/entities/ranking/ranking.service';
import { Ranking } from 'app/shared/model/ranking.model';

describe('Component Tests', () => {
  describe('Ranking Management Update Component', () => {
    let comp: RankingUpdateComponent;
    let fixture: ComponentFixture<RankingUpdateComponent>;
    let service: RankingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [RankingUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(RankingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RankingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RankingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Ranking(123);
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
        const entity = new Ranking();
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
