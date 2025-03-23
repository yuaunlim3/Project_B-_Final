import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordService } from '../../../services/password.service';
import { ResetPassword, VerifyCode } from '../../../model/model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forget',
  standalone: false,
  templateUrl: './forget.component.html',
  styleUrl: './forget.component.css'
})
export class ForgetComponent implements OnInit {

  private fb = inject(FormBuilder)
  private router = inject(Router)
  emailForm!: FormGroup;
  verificationForm!: FormGroup;
  passwordForm!: FormGroup;
  currentStep=1;
  private passwordSvc = inject(PasswordService)

  ngOnInit(): void {
    this.createForms();
  }

  createForms(): void {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    this.verificationForm = this.fb.group({
      code: ['', Validators.required]
    });

    this.passwordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, { validator: this.checkPasswords });
  }

  checkPasswords(group: FormGroup) {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { notMatching: true };
  }


  submitEmail(){
    this.passwordSvc.getCode(this.emailForm.value.email).then(result =>{
      this.currentStep = 2
    }).catch(
      error=>{
        if (error.status === 404) {
          alert("Account not found.");
        }
      }
    )
  }
  verify(){

    const code = this.verificationForm.value.code;
    const info:VerifyCode = {
      code:code,
      email:this.emailForm.value.email
    }

    this.passwordSvc.checkCode(info).then(result =>{
      this.currentStep = 3
    }).catch(
      error=>{
        if (error.status === 401) {
          alert("Code provided is invalid.");
        }
      }
    )
  }

  resendCode(){}

  resetPassword(){
    const email = this.emailForm.value.email
    const password = this.passwordForm.value.newPassword
    const info:ResetPassword = {
      email:email,
      password:password
    }

    this.passwordSvc.resetPassword(info).then(result =>{
      this.currentStep= 4
      this.passwordForm.reset()
      this.emailForm.reset()
      this.verificationForm.reset()
    }).catch(
      error=>{
        if (error.status === 409) {
          alert("Please give a new password");
          this.passwordForm.reset()
        }
      }
    )

  }

  login(){
    this.router.navigate(['healthtracker/login']);
  }

  getEmailErrorMessage() {
    const emailControl = this.emailForm.get('email');
    if (emailControl?.hasError('required')) {
      return 'Email is required';
    }
    if (emailControl?.hasError('email')) {
      return 'Please enter a valid email address';
    }
    return '';
  }

  getVerificationErrorMessage() {
    const codeControl = this.verificationForm.get('code');
    if (codeControl?.hasError('required')) {
      return 'Verification code is required';
    }
    if (codeControl?.hasError('pattern')) {
      return 'Code must be 6 digits';
    }
    return '';
  }

  getPasswordErrorMessage() {
    const passwordControl = this.passwordForm.get('newPassword');
    if (passwordControl?.hasError('required')) {
      return 'Password is required';
    }
    if (passwordControl?.hasError('minlength')) {
      return 'Password must be at least 8 characters';
    }
    if (passwordControl?.hasError('pattern')) {
      return 'Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character';
    }
    return '';
  }

  getConfirmPasswordErrorMessage() {
    const confirmPasswordControl = this.passwordForm.get('confirmPassword');
    if (confirmPasswordControl?.hasError('required')) {
      return 'Please confirm your password';
    }
    if (this.passwordForm.hasError('notMatching')) {
      return 'Passwords do not match';
    }
    return '';
  }
}
