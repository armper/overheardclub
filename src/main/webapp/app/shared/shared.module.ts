import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { OverheardclubSharedLibsModule } from './shared-libs.module';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { PostCardComponent } from './post/post-card.component';
import { TimeAgoPipe } from './pipe/time-ago.pipe';

@NgModule({
  imports: [OverheardclubSharedLibsModule, RouterModule],
  declarations: [AlertComponent, AlertErrorComponent, LoginModalComponent, HasAnyAuthorityDirective, PostCardComponent, TimeAgoPipe],
  entryComponents: [LoginModalComponent],
  exports: [
    OverheardclubSharedLibsModule,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    PostCardComponent,
    TimeAgoPipe
  ]
})
export class OverheardclubSharedModule {}
