import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  form:FormGroup;
   errorMessage: string = '';
   loading: boolean = false;

  constructor(
    private fb :FormBuilder,
    private authService: AuthService,
    private router: Router

  ) {
    this.form=this.fb.group({
      email:['',[Validators.required,Validators.email]],
      password:['',[Validators.required,Validators.minLength(8)]]
    })
   }

   onSubmit(){
    if(this.form.invalid){
      return;
    }
    this.loading=true;
    this.authService.register(this.form.value).subscribe({
      next:(response)=>{
        this.loading=false;
      console.log('Registration successful:',response);
      this.router.navigate(['/']);
      },
      error:(error)=>{
        this.loading=false;
        this.errorMessage=error.error.message || 'Registration failed. Please try again.';
      }

    });
  }
  ngOnInit() {
  }

}
