import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { PostUpdateComponent } from 'app/entities/post/post-update.component';
import { PostService } from 'app/entities/post/post.service';
import { Post } from 'app/shared/model/post.model';

describe('Component Tests', () => {
  describe('Post Management Update Component', () => {
    let comp: PostUpdateComponent;
    let fixture: ComponentFixture<PostUpdateComponent>;
    let service: PostService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [PostUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(PostUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PostUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PostService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Post(123, 'title', 'content');
        comp.account = { login: 'user', authorities: ['ROLE_USER'] } as any;
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(jasmine.objectContaining({ id: 123, title: 'title', content: 'content' }));
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Post(undefined, 'title', 'content');
        comp.account = { login: 'user', authorities: ['ROLE_USER'] } as any;
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(jasmine.objectContaining({ title: 'title', content: 'content' }));
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
