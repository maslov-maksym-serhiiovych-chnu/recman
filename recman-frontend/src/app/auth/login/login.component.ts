import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  authForm!: FormGroup;
  authError: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.authForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.authForm.invalid) {
      return;
    }

    this.authService.login(this.authForm.value).subscribe({
      next: () => {
        this.authError = null;
        this.router.navigate(['']).then();
      },
      error: (err) => {
        if (err.status === 401) {
          this.authError = 'Invalid username or password';
        } else {
          this.authError = 'Something went wrong';
        }
      }
    });
  }
}
