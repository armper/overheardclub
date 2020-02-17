import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { RankingDetailComponent } from 'app/entities/ranking/ranking-detail.component';
import { Ranking } from 'app/shared/model/ranking.model';

describe('Component Tests', () => {
  describe('Ranking Management Detail Component', () => {
    let comp: RankingDetailComponent;
    let fixture: ComponentFixture<RankingDetailComponent>;
    const route = ({ data: of({ ranking: new Ranking(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [RankingDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(RankingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RankingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load ranking on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.ranking).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
