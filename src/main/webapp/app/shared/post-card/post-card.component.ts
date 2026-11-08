import { Component, EventEmitter, Input, Output } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { IPost } from 'app/shared/model/post.model';
import { Account } from 'app/core/user/account.model';
import { IReaction, REACTIONS, hasReacted, totalReactions, authorName } from 'app/shared/model/reaction.model';
import { PostService } from 'app/entities/post/post.service';
import { LoginModalService } from 'app/core/login/login-modal.service';

@Component({
  selector: 'jhi-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss']
})
export class PostCardComponent {
  @Input() post!: IPost;
  @Input() account: Account | null = null;
  /** Show the full content instead of a preview. */
  @Input() full = false;
  /** Show the topic badge. */
  @Input() showTopic = true;
  @Output() reacted = new EventEmitter<IPost>();

  readonly reactions = REACTIONS;
  reacting = false;
  error: string | null = null;

  constructor(private postService: PostService, private loginModalService: LoginModalService) {}

  get alreadyReacted(): boolean {
    return hasReacted(this.post, this.account ? this.account.login : null);
  }

  get total(): number {
    return totalReactions(this.post);
  }

  get author(): string {
    return authorName(this.post);
  }

  get preview(): string {
    const content = this.post.content || '';
    return !this.full && content.length > 280 ? content.substring(0, 277) + '…' : content;
  }

  react(reaction: IReaction): void {
    if (!this.account) {
      this.loginModalService.open();
      return;
    }
    if (this.alreadyReacted || this.reacting || !this.post.id) {
      return;
    }
    this.reacting = true;
    this.error = null;
    this.postService.react(this.post.id, reaction.id).subscribe(
      res => {
        this.reacting = false;
        if (res.body) {
          Object.assign(this.post, res.body);
          this.reacted.emit(this.post);
        }
      },
      (err: HttpErrorResponse) => {
        this.reacting = false;
        this.error = err.status === 400 ? 'You already reacted to this post.' : 'Could not save your reaction, please try again.';
      }
    );
  }
}
