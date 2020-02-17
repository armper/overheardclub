import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPost } from 'app/shared/model/post.model';
import { PostService } from './post.service';
import { PostDeleteDialogComponent } from './post-delete-dialog.component';

@Component({
  selector: 'jhi-post',
  templateUrl: './post.component.html'
})
export class PostComponent implements OnInit, OnDestroy {
  posts?: IPost[];
  eventSubscriber?: Subscription;

  constructor(protected postService: PostService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.postService.query().subscribe((res: HttpResponse<IPost[]>) => (this.posts = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPosts();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPost): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPosts(): void {
    this.eventSubscriber = this.eventManager.subscribe('postListModification', () => this.loadAll());
  }

  delete(post: IPost): void {
    const modalRef = this.modalService.open(PostDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.post = post;
  }
}
