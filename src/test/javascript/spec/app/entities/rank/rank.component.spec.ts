import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { OverheardclubTestModule } from '../../../test.module';
import { RankComponent } from 'app/entities/rank/rank.component';
import { RankService } from 'app/entities/rank/rank.service';
import { Rank } from 'app/shared/model/rank.model';

describe('Component Tests', () => {
  describe('Rank Management Component', () => {
    let comp: RankComponent;
    let fixture: ComponentFixture<RankComponent>;
    let service: RankService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [RankComponent]
      })
        .overrideTemplate(RankComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RankComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RankService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Rank(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.ranks && comp.ranks[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
