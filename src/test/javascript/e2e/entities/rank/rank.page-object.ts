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

  rankTypeSelect = element(by.id('field_rankType'));
  dateInput = element(by.id('field_date'));

  userSelect = element(by.id('field_user'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
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

  async userSelectLastOption(): Promise<void> {
    await this.userSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async userSelectOption(option: string): Promise<void> {
    await this.userSelect.sendKeys(option);
  }

  getUserSelect(): ElementFinder {
    return this.userSelect;
  }

  async getUserSelectedOption(): Promise<string> {
    return await this.userSelect.element(by.css('option:checked')).getText();
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
