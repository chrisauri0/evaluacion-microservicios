import { Component, inject, computed } from '@angular/core';
import { PostService } from '../../services/post-card/post-card-service';

@Component({
  selector: 'app-historial',
  standalone: true,
  templateUrl: './historial.html',
})
export class HistorialComponent {
  private postService = inject(PostService);

  // Mismo criterio que usa Home al publicar: authorId: 'me'
  myPosts = computed(() => this.postService.posts().filter((p) => p.authorId === 'me'));

  onDelete(id: string): void {
    this.postService.delete(id);
  }
}