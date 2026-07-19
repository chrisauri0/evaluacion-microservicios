import { Component, inject, signal, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { HistorialService } from '../../services/historial/historial-service';
import { PostBackend, PostCategory } from '../../models/post-card/post-card-model';

@Component({
  selector: 'app-historial',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './historial.html',
})
export class HistorialComponent {
  private historialService = inject(HistorialService);

  categories: PostCategory[] = ['Anuncios', 'General', 'Preguntas', 'Humor', 'Noticias'];

  categoryFilter = signal<PostCategory | 'todas'>('todas');
  dateFrom = signal<string>('');
  dateTo = signal<string>('');

  posts = signal<PostBackend[]>([]);
  isLoading = signal(true);
  errorMsg = signal<string | null>(null);

  constructor() {
    // se re-ejecuta automáticamente cada vez que cambia algún filtro
    effect(() => {
      this.categoryFilter();
      this.dateFrom();
      this.dateTo();
      this.cargar();
    });
  }

  private cargar(): void {
    this.isLoading.set(true);
    this.errorMsg.set(null);

    this.historialService
      .misPublicaciones({
        categoria: this.categoryFilter() !== 'todas' ? this.categoryFilter() : undefined,
        desde: this.dateFrom() || undefined,
        hasta: this.dateTo() || undefined,
      })
      .subscribe({
        next: (data) => {
          this.posts.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Error al cargar mis publicaciones', err);
          this.errorMsg.set('No se pudieron cargar tus publicaciones.');
          this.isLoading.set(false);
        },
      });
  }

  clearFilters(): void {
    this.categoryFilter.set('todas');
    this.dateFrom.set('');
    this.dateTo.set('');
  }

  onDelete(id: number): void {
    if (!confirm('¿Eliminar esta publicación?')) return;

    this.historialService.eliminar(id).subscribe({
      next: () => this.posts.update((list) => list.filter((p) => p.id !== id)),
      error: (err) => console.error('Error al eliminar', err),
    });
  }
}