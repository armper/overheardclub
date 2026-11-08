import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { OverheardclubTestModule } from '../../../test.module';
import { PostCardComponent } from 'app/shared/post-card/post-card.component';
import { PostService } from 'app/entities/post/post.service';
import { LoginModalService } from 'app/core/login/login-modal.service';
import { Post } from 'app/shared/model/post.model';
import { REACTIONS } from 'app/shared/model/reaction.model';

describe('Component Tests', () => {
  describe('PostCardComponent', () => {
    let comp: PostCardComponent;
    let fixture: ComponentFixture<PostCardComponent>;
    let service: PostService;
    let loginModalService: LoginModalService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [OverheardclubTestModule],
        declarations: [PostCardComponent],
        providers: [{ provide: LoginModalService, useValue: { open: jasmine.createSpy('open') } }]
      })
        .overrideTemplate(PostCardComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PostCardComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PostService);
      loginModalService = fixture.debugElement.injector.get(LoginModalService);
      comp.post = new Post(7, 'title', 'content');
      comp.post.userUpranks = [];
    });

    it('asks anonymous visitors to sign in instead of reacting', () => {
      spyOn(service, 'react');
      comp.account = null;

      comp.react(REACTIONS[0]);

      expect(loginModalService.open).toHaveBeenCalled();
      expect(service.react).not.toHaveBeenCalled();
    });

    it('sends the reaction and updates the counts', () => {
      comp.account = { login: 'user', authorities: ['ROLE_USER'] } as any;
      const updated = new Post(7, 'title', 'content');
      updated.rankThree = 1;
      updated.userUpranks = [{ login: 'user' }];
      spyOn(service, 'react').and.returnValue(of(new HttpResponse({ body: updated })));

      comp.react(REACTIONS[2]);

      expect(service.react).toHaveBeenCalledWith(7, 3);
      expect(comp.post.rankThree).toEqual(1);
      expect(comp.total).toEqual(1);
      expect(comp.alreadyReacted).toBe(true);
    });

    it('does not react twice', () => {
      comp.account = { login: 'user', authorities: ['ROLE_USER'] } as any;
      comp.post.userUpranks = [{ login: 'user' }];
      spyOn(service, 'react');

      comp.react(REACTIONS[0]);

      expect(service.react).not.toHaveBeenCalled();
    });

    it('shows an error when the server rejects the reaction', () => {
      comp.account = { login: 'user', authorities: ['ROLE_USER'] } as any;
      spyOn(service, 'react').and.returnValue(throwError(new HttpErrorResponse({ status: 400 })));

      comp.react(REACTIONS[0]);

      expect(comp.error).toContain('already reacted');
      expect(comp.reacting).toBe(false);
    });

    it('truncates long content in preview mode only', () => {
      comp.post.content = 'x'.repeat(400);
      expect(comp.preview.length).toBeLessThan(300);
      comp.full = true;
      expect(comp.preview.length).toEqual(400);
    });
  });
});
