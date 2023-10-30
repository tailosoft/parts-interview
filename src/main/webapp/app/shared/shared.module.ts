import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { PrimeNGCommonModule } from './primeng-common.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import FindLanguageFromKeyPipe from './language/find-language-from-key.pipe';
import TranslateDirective from './language/translate.directive';

/**
 * Application wide Module
 */
@NgModule({
  imports: [PrimeNGCommonModule, FindLanguageFromKeyPipe, TranslateDirective],
  exports: [CommonModule, NgbModule, PrimeNGCommonModule, FontAwesomeModule, TranslateModule, FindLanguageFromKeyPipe, TranslateDirective],
})
export default class SharedModule {}
