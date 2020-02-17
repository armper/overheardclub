import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { OverheardclubTestModule } from '../../../test.module';
import { RankingComponent } from 'app/entities/ranking/ranking.component';
import { RankingService } from 'app/entities/ranking/ranking.service';
import { Ranking } from 'app/shared/model/ranking.model';

describe('Component Tests', () => {
  describe('Ranking Management Component', () => {
    let comp: RankingComponent;
    let fixture: ComponentFixture<RankingComponent>;
    let service: RankingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [RankingComponent]
      })
        .overrideTemplate(RankingComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RankingComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RankingService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Ranking(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.rankings && comp.rankings[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
