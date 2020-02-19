import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { OverheardCommentDetailComponent } from 'app/entities/overheard-comment/overheard-comment-detail.component';
import { OverheardComment } from 'app/shared/model/overheard-comment.model';

describe('Component Tests', () => {
  describe('OverheardComment Management Detail Component', () => {
    let comp: OverheardCommentDetailComponent;
    let fixture: ComponentFixture<OverheardCommentDetailComponent>;
    const route = ({ data: of({ overheardComment: new OverheardComment(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [OverheardCommentDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(OverheardCommentDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OverheardCommentDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load overheardComment on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.overheardComment).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
