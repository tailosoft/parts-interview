import { Pipe, PipeTransform } from '@angular/core';

import dayjs from 'dayjs/esm';
import duration from 'dayjs/esm/plugin/duration';

@Pipe({
  standalone: true,
  name: 'duration',
})
export class DurationPipe implements PipeTransform {
  constructor() {
    dayjs.extend(duration);
  }
  transform(value: any): string {
    if (value) {
      return dayjs.duration(value).humanize();
    }
    return '';
  }
}
