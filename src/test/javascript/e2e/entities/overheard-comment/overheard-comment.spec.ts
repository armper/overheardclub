import { browser, ExpectedConditions as ec /* , protractor, promise */ } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import {
  OverheardCommentComponentsPage,
  /* OverheardCommentDeleteDialog, */
  OverheardCommentUpdatePage
} from './overheard-comment.page-object';

const expect = chai.expect;

describe('OverheardComment e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let overheardCommentComponentsPage: OverheardCommentComponentsPage;
  let overheardCommentUpdatePage: OverheardCommentUpdatePage;
  /* let overheardCommentDeleteDialog: OverheardCommentDeleteDialog; */

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load OverheardComments', async () => {
    await navBarPage.goToEntity('overheard-comment');
    overheardCommentComponentsPage = new OverheardCommentComponentsPage();
    await browser.wait(ec.visibilityOf(overheardCommentComponentsPage.title), 5000);
    expect(await overheardCommentComponentsPage.getTitle()).to.eq('Overheard Comments');
    await browser.wait(
      ec.or(ec.visibilityOf(overheardCommentComponentsPage.entities), ec.visibilityOf(overheardCommentComponentsPage.noResult)),
      1000
    );
  });

  it('should load create OverheardComment page', async () => {
    await overheardCommentComponentsPage.clickOnCreateButton();
    overheardCommentUpdatePage = new OverheardCommentUpdatePage();
    expect(await overheardCommentUpdatePage.getPageTitle()).to.eq('Create or edit a Overheard Comment');
    await overheardCommentUpdatePage.cancel();
  });

  /* it('should create and save OverheardComments', async () => {
        const nbButtonsBeforeCreate = await overheardCommentComponentsPage.countDeleteButtons();

        await overheardCommentComponentsPage.clickOnCreateButton();

        await promise.all([
            overheardCommentUpdatePage.setContentInput('content'),
            overheardCommentUpdatePage.setDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
            overheardCommentUpdatePage.setRankingInput('5'),
            overheardCommentUpdatePage.userSelectLastOption(),
            overheardCommentUpdatePage.postSelectLastOption(),
        ]);

        expect(await overheardCommentUpdatePage.getContentInput()).to.eq('content', 'Expected Content value to be equals to content');
        expect(await overheardCommentUpdatePage.getDateInput()).to.contain('2001-01-01T02:30', 'Expected date value to be equals to 2000-12-31');
        expect(await overheardCommentUpdatePage.getRankingInput()).to.eq('5', 'Expected ranking value to be equals to 5');

        await overheardCommentUpdatePage.save();
        expect(await overheardCommentUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await overheardCommentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /* it('should delete last OverheardComment', async () => {
        const nbButtonsBeforeDelete = await overheardCommentComponentsPage.countDeleteButtons();
        await overheardCommentComponentsPage.clickOnLastDeleteButton();

        overheardCommentDeleteDialog = new OverheardCommentDeleteDialog();
        expect(await overheardCommentDeleteDialog.getDialogTitle())
            .to.eq('Are you sure you want to delete this Overheard Comment?');
        await overheardCommentDeleteDialog.clickOnConfirmButton();

        expect(await overheardCommentComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
