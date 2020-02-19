import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { OverheardCommentUpdateComponent } from 'app/entities/overheard-comment/overheard-comment-update.component';
import { OverheardCommentService } from 'app/entities/overheard-comment/overheard-comment.service';
import { OverheardComment } from 'app/shared/model/overheard-comment.model';

describe('Component Tests', () => {
  describe('OverheardComment Management Update Component', () => {
    let comp: OverheardCommentUpdateComponent;
    let fixture: ComponentFixture<OverheardCommentUpdateComponent>;
    let service: OverheardCommentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [OverheardCommentUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(OverheardCommentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OverheardCommentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OverheardCommentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new OverheardComment(123);
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
        const entity = new OverheardComment();
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
