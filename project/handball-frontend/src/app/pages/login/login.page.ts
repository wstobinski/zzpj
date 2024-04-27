import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";

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
              private utils: Utils) { }

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
        password: ['', [Validators.required, Validators.minLength(8)]]
      });
    }
  }

  onSubmit() {
    const requestObj = this.loginForm.getRawValue();
    console.log(requestObj);
    console.log(this.loginForm)
  }

  getCardTitle() {
    return this.loginFormPresented ? "Login to Handball League" : "Activate your Handball League Account"
  }

  getCardSubtitle() {
    return this.loginFormPresented ? "Got activation code? Activate your account here!" : "Already have an account? Login here!"
  }

  onSubmitActivation() {

  }

  formHasError(formGroup: any, fieldName: string, errorType: string) {
    return this.utils.formHasError(formGroup, fieldName, errorType);
  }
}
