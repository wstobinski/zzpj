import {Component, OnDestroy, OnInit} from '@angular/core';
import {LoadingService} from "../../services/loading.service";
import {Subscription} from "rxjs";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {UserService} from "../../services/user.service";
import {User} from "../../model/user.model";
import {ChangePasswordDto} from "../../model/DTO/change-password.dto";

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
  oldPwdType: 'password' | 'text' = 'password';
  showPwdIcon: 'eye-off-outline' | 'eye-outline' = 'eye-off-outline';
  showOldPwdIcon: 'eye-off-outline' | 'eye-outline' = 'eye-off-outline';
  ngOnInit() {
    this.userSub = this.userService.getUser().subscribe(user => {
      this.user = user;
      this.accountFormGroup = this.formBuilder.group({
        oldPassword: ['', [Validators.required, Validators.minLength(8)]],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
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
  onChangePwdType(type: 'old' | 'new') {

    if (type === "new") {
      if(this.pwdType === 'password'){
        this.pwdType = 'text';
        this.showPwdIcon = 'eye-outline';
      } else {
        this.pwdType = 'password';
        this.showPwdIcon = 'eye-off-outline';
      }
    } else {
      if(this.oldPwdType === 'password'){
        this.oldPwdType = 'text';
        this.showOldPwdIcon = 'eye-outline';
      } else {
        this.oldPwdType = 'password';
        this.showOldPwdIcon = 'eye-off-outline';
      }
    }


  }

  onPasswordChanged() {
    const rawFormValue: ChangePasswordDto = this.accountFormGroup.getRawValue();
    this.userService.changePassword(rawFormValue).then(r => {
      if (r.ok) {
        this.accountFormGroup.get('oldPassword').reset();
        this.accountFormGroup.get('newPassword').reset();
        this.accountFormGroup.get('passwordConfirm').reset();
        this.utils.presentInfoToast('Hasło zmieniono pomyślnie');
      } else {
        this.utils.presentAlertToast('Wystąpił błąd podczas zmiany hasła');
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast('Wystąpił błąd podczas zmiany hasła. Twoja sesja wygasła, zaloguj się ponownie');
      } else {
        this.utils.presentAlertToast('Wystąpił błąd podczas zmiany hasła');
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
