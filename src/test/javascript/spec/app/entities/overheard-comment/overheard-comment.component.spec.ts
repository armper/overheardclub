import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { OverheardclubTestModule } from '../../../test.module';
import { OverheardCommentComponent } from 'app/entities/overheard-comment/overheard-comment.component';
import { OverheardCommentService } from 'app/entities/overheard-comment/overheard-comment.service';
import { OverheardComment } from 'app/shared/model/overheard-comment.model';

describe('Component Tests', () => {
  describe('OverheardComment Management Component', () => {
    let comp: OverheardCommentComponent;
    let fixture: ComponentFixture<OverheardCommentComponent>;
    let service: OverheardCommentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [OverheardCommentComponent]
      })
        .overrideTemplate(OverheardCommentComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OverheardCommentComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OverheardCommentService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new OverheardComment(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.overheardComments && comp.overheardComments[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
