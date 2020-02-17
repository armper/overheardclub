import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { RankDetailComponent } from 'app/entities/rank/rank-detail.component';
import { Rank } from 'app/shared/model/rank.model';

describe('Component Tests', () => {
  describe('Rank Management Detail Component', () => {
    let comp: RankDetailComponent;
    let fixture: ComponentFixture<RankDetailComponent>;
    const route = ({ data: of({ rank: new Rank(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [RankDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(RankDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RankDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load rank on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.rank).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
