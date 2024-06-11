import {Observable, of} from "rxjs";

export class MockApiService {
  // Mock methods and properties as needed
  get(url: string): Observable<any> {
    return of([]);  // or some mock data
  }
  // Add other mock methods as needed
}
