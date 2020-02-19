import { element, by, ElementFinder } from 'protractor';

export class PostComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-post div table .btn-danger'));
  title = element.all(by.css('jhi-post div h2#page-heading span')).first();
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

export class PostUpdatePage {
  pageTitle = element(by.id('jhi-post-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  titleInput = element(by.id('field_title'));
  contentInput = element(by.id('field_content'));
  dateInput = element(by.id('field_date'));
  rankOneInput = element(by.id('field_rankOne'));
  rankTwoInput = element(by.id('field_rankTwo'));
  rankThreeInput = element(by.id('field_rankThree'));
  rankFourInput = element(by.id('field_rankFour'));
  rankFiveInput = element(by.id('field_rankFive'));

  userSelect = element(by.id('field_user'));
  topicSelect = element(by.id('field_topic'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setTitleInput(title: string): Promise<void> {
    await this.titleInput.sendKeys(title);
  }

  async getTitleInput(): Promise<string> {
    return await this.titleInput.getAttribute('value');
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

  async setRankOneInput(rankOne: string): Promise<void> {
    await this.rankOneInput.sendKeys(rankOne);
  }

  async getRankOneInput(): Promise<string> {
    return await this.rankOneInput.getAttribute('value');
  }

  async setRankTwoInput(rankTwo: string): Promise<void> {
    await this.rankTwoInput.sendKeys(rankTwo);
  }

  async getRankTwoInput(): Promise<string> {
    return await this.rankTwoInput.getAttribute('value');
  }

  async setRankThreeInput(rankThree: string): Promise<void> {
    await this.rankThreeInput.sendKeys(rankThree);
  }

  async getRankThreeInput(): Promise<string> {
    return await this.rankThreeInput.getAttribute('value');
  }

  async setRankFourInput(rankFour: string): Promise<void> {
    await this.rankFourInput.sendKeys(rankFour);
  }

  async getRankFourInput(): Promise<string> {
    return await this.rankFourInput.getAttribute('value');
  }

  async setRankFiveInput(rankFive: string): Promise<void> {
    await this.rankFiveInput.sendKeys(rankFive);
  }

  async getRankFiveInput(): Promise<string> {
    return await this.rankFiveInput.getAttribute('value');
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

  async topicSelectLastOption(): Promise<void> {
    await this.topicSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async topicSelectOption(option: string): Promise<void> {
    await this.topicSelect.sendKeys(option);
  }

  getTopicSelect(): ElementFinder {
    return this.topicSelect;
  }

  async getTopicSelectedOption(): Promise<string> {
    return await this.topicSelect.element(by.css('option:checked')).getText();
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

export class PostDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-post-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-post'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
