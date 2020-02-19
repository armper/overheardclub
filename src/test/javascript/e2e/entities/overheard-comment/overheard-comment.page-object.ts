import { element, by, ElementFinder } from 'protractor';

export class OverheardCommentComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-overheard-comment div table .btn-danger'));
  title = element.all(by.css('jhi-overheard-comment div h2#page-heading span')).first();
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

export class OverheardCommentUpdatePage {
  pageTitle = element(by.id('jhi-overheard-comment-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  contentInput = element(by.id('field_content'));
  dateInput = element(by.id('field_date'));
  rankingInput = element(by.id('field_ranking'));

  userSelect = element(by.id('field_user'));
  postSelect = element(by.id('field_post'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setContentInput(content: string): Promise<void> {
    await this.contentInput.sendKeys(content);
  }

  async getContentInput(): Promise<string> {
    return await this.contentInput.getAttribute('value');
  }

  async setDateInput(date: string): Promise<void> {
    await this.dateInput.sendKeys(date);
  }

  async getDateInput(): Promise<string> {
    return await this.dateInput.getAttribute('value');
  }

  async setRankingInput(ranking: string): Promise<void> {
    await this.rankingInput.sendKeys(ranking);
  }

  async getRankingInput(): Promise<string> {
    return await this.rankingInput.getAttribute('value');
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

  async postSelectLastOption(): Promise<void> {
    await this.postSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async postSelectOption(option: string): Promise<void> {
    await this.postSelect.sendKeys(option);
  }

  getPostSelect(): ElementFinder {
    return this.postSelect;
  }

  async getPostSelectedOption(): Promise<string> {
    return await this.postSelect.element(by.css('option:checked')).getText();
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

export class OverheardCommentDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-overheardComment-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-overheardComment'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
