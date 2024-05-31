import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'roleTranslate',
  standalone: true
})
export class RoleTranslatePipe implements PipeTransform {

  transform(value: "admin" | "captain" | "arbiter", ...args: unknown[]): unknown {
    if (value === "admin") {
      return value;
    }
    if (value === "captain") {
      return "kapitan"
    }
    return "sędzia"
  }

}
