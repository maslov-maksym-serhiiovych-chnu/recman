import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService, RegisterRequest} from '../auth.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  authForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.authForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]]
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
        console.error('Registration failed', err);
      }
    });
  }
}
