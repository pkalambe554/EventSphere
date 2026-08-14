import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
    standalone: true,
  imports: [CommonModule, ReactiveFormsModule ,],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form:FormGroup;
  errorMessage: string = '';
  loading:boolean=false;
  constructor(private route: ActivatedRoute,
    private authService: AuthService ,
              private fb: FormBuilder,
              private router : Router) {
               this.form=this.fb.group({
                email:['',[Validators.required,Validators.email]],
                password:['',[Validators.required]]
               }) 
               }

  onSubmit(){
    if(this.form.invalid){
      return;
    }
    this.loading=true;
    this.authService.login(this.form.value).subscribe({
      next:(res)=>{
        this.loading=false;
        console.log('Login successful:',res);
        this.authService.saveToken(res.accessToken);
        this.authService.saveRole(res.role);
        this.router.navigate(['/']);
        },
        error:(err)=>{
          this.loading=false;
          this.errorMessage=err.error.message || 'Login failed. Please try again.';
        }
    })
  }

  ngOnInit(): void {
  if (this.route.snapshot.queryParamMap.get('sessionExpired')) {
    this.errorMessage = 'Your session expired. Please log in again.';
  }
}

}
