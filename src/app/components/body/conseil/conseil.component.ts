import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-conseil',
  standalone: true,
  imports: [ FormsModule,CommonModule,ReactiveFormsModule],
  templateUrl: './conseil.component.html',
  styleUrl: './conseil.component.css'
})
export class ConseilComponent {

  
}
