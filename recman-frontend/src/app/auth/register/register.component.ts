import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService, RegisterRequest} from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {
  authForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.authForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSubmit(): void {
    if (this.authForm.invalid) {
      return;
    }

    const {username, password} = this.authForm.value;

    const req: RegisterRequest = {username, password};

    this.authService.register(req).subscribe({
      next: () => {
        this.router.navigate(['/auth/login']).then();
      },
      error: (err) => {
        if (err.status === 409) {
          this.authForm.get('username')?.setErrors({usernameExists: true});
        }
      }
    });
  }
}
