import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPost } from 'app/shared/model/post.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'jhi-post-detail',
  templateUrl: './post-detail.component.html'
})
export class PostDetailComponent implements OnInit {
  post: IPost | null = null;
  authSubscription!: Subscription;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ post }) => (this.post = post));
  }

  previousState(): void {
    window.history.back();
  }
}
