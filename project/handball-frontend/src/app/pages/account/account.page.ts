import {Component, OnDestroy, OnInit} from '@angular/core';
import {LoadingService} from "../../services/loading.service";
import {Subscription} from "rxjs";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {UserService} from "../../services/user.service";
import {User} from "../../model/user.model";

@Component({
  selector: 'app-account',
  templateUrl: './account.page.html',
  styleUrls: ['./account.page.scss'],
})
export class AccountPage implements OnInit, OnDestroy {

  constructor(private loadingService: LoadingService,
              private formBuilder: FormBuilder,
              private utils: Utils,
              private userService: UserService) {

  }
  user: User;
  isLoading: boolean = false;
  hasUnsavedChanges: boolean = false;
  loadingSub: Subscription;
  userSub: Subscription;
  accountFormGroup: FormGroup;
  pwdType: 'password' | 'text' = 'password';
  showPwdIcon: 'eye-off-outline' | 'eye-outline' = 'eye-off-outline';
  ngOnInit() {
    this.userSub = this.userService.getUser().subscribe(user => {
      this.user = user;
      this.accountFormGroup = this.formBuilder.group({
        email: [this.user?.email, [Validators.required, Validators.email]],
        role: [this.user?.role, [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        passwordConfirm: ['', [Validators.required]]
      }, {validators: this.utils.passwordMismatchValidator});
    });
    this.loadingSub = this.loadingService.isLoading.subscribe(r => {
      this.isLoading = r;
    });

  }

  ngOnDestroy(): void {
    if (this.loadingSub) {
      this.loadingSub.unsubscribe();
    }
  }

  markUnsavedChanges() {
    if (!this.hasUnsavedChanges) {
      this.hasUnsavedChanges = true;
    }
  }
  onChangePwdType() {
    if(this.pwdType === 'password'){
      this.pwdType = 'text';
      this.showPwdIcon = 'eye-outline';
    } else {
      this.pwdType = 'password';
      this.showPwdIcon = 'eye-off-outline';
    }
  }

  onAccountEdit() {
    this.user = Object.assign(this.user, this.accountFormGroup.value);
    console.log(this.user);
    this.userService.updateUser(this.user.uuid, this.user).then(r => {
      if (r.ok) {
        this.userService.setUser(r.response);
        this.user = r.response;
        this.accountFormGroup.get('password').reset();
        this.accountFormGroup.get('passwordConfirm').reset();
        this.utils.presentInfoToast('Edycja konta zakończona pomyślnie');
      } else {
        this.utils.presentAlertToast('Wystąpił błąd podczas edycji konta');
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast('Wystąpił błąd podczas edycji konta. Twoja sesja wygasła, zaloguj się ponownie');
      } else {
        this.utils.presentAlertToast('Wystąpił błąd podczas edycji konta');
      }
    })
  }

  formControlHasError(fieldName: string, errorType: string) {
   return this.utils.formHasError(this.accountFormGroup, fieldName, errorType);
  }
  formHasError(errorType: string) {
    return this.accountFormGroup.hasError(errorType) && this.accountFormGroup.touched;
  }

  protected readonly console = console;

  logTest() {
    console.log(this.accountFormGroup)
  }
}
