import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'refereeRating',
  standalone: true
})
export class RefereeRatingPipe implements PipeTransform {

  transform(value: number, ...args: unknown[]): unknown {
    if (value == 0.0) {
      return "-";
    }
    if (value < -0.6) {
      return "Bardzo słaba"
    } else if (value > -0.6 && value < -0.2) {
      return "Słaba"
    } else if (value > -0.2 && value < 0.4) {
      return "Dobra"
    }
    return "Bardzo dobra";
  }

}
