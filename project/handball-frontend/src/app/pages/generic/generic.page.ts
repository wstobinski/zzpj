import {Component, OnDestroy, OnInit} from '@angular/core';
import {LoadingService} from "../../services/loading.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-generic',
  templateUrl: './generic.page.html',
  styleUrls: ['./generic.page.scss'],
})
export class GenericPage implements OnInit, OnDestroy {

  constructor(private loadingService: LoadingService) { }

  isLoading: boolean = false;
  loadingSub: Subscription;
  ngOnInit() {
    this.loadingSub = this.loadingService.isLoading.subscribe(isLoading => {
      this.isLoading = isLoading;
    })
  }

  ngOnDestroy(): void {
    if (this.loadingSub) {
      this.loadingSub.unsubscribe();
    }
  }

}
