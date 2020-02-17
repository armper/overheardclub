import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { OverheardclubSharedModule } from 'app/shared/shared.module';
import { OverheardclubCoreModule } from 'app/core/core.module';
import { OverheardclubAppRoutingModule } from './app-routing.module';
import { OverheardclubHomeModule } from './home/home.module';
import { OverheardclubEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    OverheardclubSharedModule,
    OverheardclubCoreModule,
    OverheardclubHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    OverheardclubEntityModule,
    OverheardclubAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class OverheardclubAppModule {}
