import { Component, OnInit } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {AuthService} from "../../services/auth.service";
import {UserService} from "../../services/user.service";

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
              private userService: UserService) { }

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
        code: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(8)]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        passwordConfirm: ['', [Validators.required, Validators.minLength(8)]]
      }, {validators: this.passwordMismatchValidator});
    }
  }

  onLogin() {
    const requestObj = this.loginForm.getRawValue();
    console.log(requestObj);
    this.userService.login(requestObj).then(r => console.log(r));


  }
  onActivate() {

  }

  getCardTitle() {
    return this.loginFormPresented ? "Login to Handball League" : "Activate your Handball League Account"
  }

  getCardSubtitle() {
    return this.loginFormPresented ? "Got activation code? Activate your account here!" : "Already have an account? Login here!"
  }



  formControlHasError(formGroup: any, fieldName: string, errorType: string) {
    return this.utils.formHasError(formGroup, fieldName, errorType);
  }
  formHasError(formGroup: FormGroup, errorType: string) {
    return formGroup.hasError(errorType) && formGroup.touched;
  }

  passwordMismatchValidator: ValidatorFn = (
    control: AbstractControl,
  ): ValidationErrors | null => {
    const password = control.get('password');
    const passwordConfirm = control.get('passwordConfirm');

    return password && passwordConfirm && password.value !== passwordConfirm.value
      ? { passwordMismatch: true }
      : null;
  };

}
