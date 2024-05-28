import { Component, OnInit } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {AuthService} from "../../services/auth.service";
import {UserService} from "../../services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../model/user.model";

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  loginForm: FormGroup;
  activationForm: FormGroup;
  loginFormPresented: boolean = true;


  constructor(private formBuilder: FormBuilder,
              private utils: Utils,
              private userService: UserService,
              private authService: AuthService,
              private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {

    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });

  }

  toggleFormType() {
    this.loginFormPresented = !this.loginFormPresented;
    if (this.activationForm == null) {
      this.activationForm = this.formBuilder.group({
        code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        passwordConfirm: ['', [Validators.required]]
      }, {validators: this.utils.passwordMismatchValidator});
    }
  }

  onLogin() {
    const requestObj = this.loginForm.getRawValue();
    console.log(requestObj);
    this.userService.login(requestObj).then(r => {
      console.log(r);
      this.authService.setUserAuthData(r.response.token);
      this.userService.setUser(r.response.user);
      console.log(this.route);
      const redirectURL = this.route.snapshot.params['returnUrl'] || '/home';
      this.router.navigateByUrl(redirectURL);
    });


  }

  loginWithActivateData(activateData) {
    this.userService.login(activateData).then(r => {
      console.log(r);
      this.authService.setUserAuthData(r.response.token);
      this.userService.setUser(r.response.user);
      console.log(this.route);
      const redirectURL = this.route.snapshot.params['returnUrl'] || '/home';
      this.router.navigateByUrl(redirectURL);
    });


  }

  onActivate() {

    const rawForm = this.activationForm.getRawValue();
    rawForm.code = Number(rawForm.code);
    this.userService.activateAccount(rawForm).then(r => {
      if (r.ok) {
        this.utils.presentInfoToast("Konto aktywowano pomyślnie!");
        const activatedUser = r.response as User;
        const loginData = {email: activatedUser.email, password: rawForm.password};
        this.loginWithActivateData(loginData);
      } else {
        this.utils.presentAlertToast("Wystąpił problem podczas aktywacji konta");
      }
    }).catch(e => {
      if (e.status === 401) {
        this.utils.presentAlertToast("Wystąpił problem podczas aktywacji konta. Twoja sesja wygasła, zaloguj się ponownie");
      } else {
        this.utils.presentAlertToast("Wystąpił problem podczas aktywacji konta");
      }
    })

  }

  getCardTitle() {
    return this.loginFormPresented ? "Zaloguj się do Handball League" : "Aktywuj swoje konto Handball League"
  }

  getCardSubtitle() {
    return this.loginFormPresented ? "Masz kod aktywacyjny? Aktywuj konto tutaj!" : "Masz już konto? Zaloguj się tutaj!"
  }



  formControlHasError(formGroup: any, fieldName: string, errorType: string) {
    return this.utils.formHasError(formGroup, fieldName, errorType);
  }
  formHasError(formGroup: FormGroup, errorType: string) {
    return formGroup.hasError(errorType) && formGroup.touched;
  }



}
