import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { RankingComponentsPage, RankingDeleteDialog, RankingUpdatePage } from './ranking.page-object';

const expect = chai.expect;

describe('Ranking e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let rankingComponentsPage: RankingComponentsPage;
  let rankingUpdatePage: RankingUpdatePage;
  let rankingDeleteDialog: RankingDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Rankings', async () => {
    await navBarPage.goToEntity('ranking');
    rankingComponentsPage = new RankingComponentsPage();
    await browser.wait(ec.visibilityOf(rankingComponentsPage.title), 5000);
    expect(await rankingComponentsPage.getTitle()).to.eq('Rankings');
    await browser.wait(ec.or(ec.visibilityOf(rankingComponentsPage.entities), ec.visibilityOf(rankingComponentsPage.noResult)), 1000);
  });

  it('should load create Ranking page', async () => {
    await rankingComponentsPage.clickOnCreateButton();
    rankingUpdatePage = new RankingUpdatePage();
    expect(await rankingUpdatePage.getPageTitle()).to.eq('Create or edit a Ranking');
    await rankingUpdatePage.cancel();
  });

  it('should create and save Rankings', async () => {
    const nbButtonsBeforeCreate = await rankingComponentsPage.countDeleteButtons();

    await rankingComponentsPage.clickOnCreateButton();

    await promise.all([
      rankingUpdatePage.rankTypeSelectLastOption(),
      rankingUpdatePage.setDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      rankingUpdatePage.userSelectLastOption(),
      rankingUpdatePage.postSelectLastOption()
    ]);

    expect(await rankingUpdatePage.getDateInput()).to.contain('2001-01-01T02:30', 'Expected date value to be equals to 2000-12-31');

    await rankingUpdatePage.save();
    expect(await rankingUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await rankingComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Ranking', async () => {
    const nbButtonsBeforeDelete = await rankingComponentsPage.countDeleteButtons();
    await rankingComponentsPage.clickOnLastDeleteButton();

    rankingDeleteDialog = new RankingDeleteDialog();
    expect(await rankingDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Ranking?');
    await rankingDeleteDialog.clickOnConfirmButton();

    expect(await rankingComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
