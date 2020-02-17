import { element, by, ElementFinder } from 'protractor';

export class RankComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-rank div table .btn-danger'));
  title = element.all(by.css('jhi-rank div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getText();
  }
}

export class RankUpdatePage {
  pageTitle = element(by.id('jhi-rank-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  rankInput = element(by.id('field_rank'));
  rankTypeSelect = element(by.id('field_rankType'));
  dateInput = element(by.id('field_date'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setRankInput(rank: string): Promise<void> {
    await this.rankInput.sendKeys(rank);
  }

  async getRankInput(): Promise<string> {
    return await this.rankInput.getAttribute('value');
  }

  async setRankTypeSelect(rankType: string): Promise<void> {
    await this.rankTypeSelect.sendKeys(rankType);
  }

  async getRankTypeSelect(): Promise<string> {
    return await this.rankTypeSelect.element(by.css('option:checked')).getText();
  }

  async rankTypeSelectLastOption(): Promise<void> {
    await this.rankTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setDateInput(date: string): Promise<void> {
    await this.dateInput.sendKeys(date);
  }

  async getDateInput(): Promise<string> {
    return await this.dateInput.getAttribute('value');
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class RankDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-rank-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-rank'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
