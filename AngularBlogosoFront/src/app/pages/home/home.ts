import { Component, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PostService } from '../../services/post-card/post-card-service';
import { PostCategory } from '../../models/post-card/post-card-model';
import { PostCardComponent } from '../../components/post-card/post-card';
import { FriendService } from '../../services/amigos/amigos-services';
import {CommonModule} from "@angular/common";
import { AuthService } from './../../auth/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, PostCardComponent, CommonModule],
  templateUrl: './home.html',
})
export class HomeComponent {
  private postService = inject(PostService);
  private friendService = inject(FriendService);

  posts = this.postService.posts;
  categories: PostCategory[] = ['Anuncios', 'General', 'Preguntas', 'Humor', 'Noticias'];

  newContent = signal('');
  newCategory = signal<PostCategory>('General');

  activeTab = signal<'todos' | 'amigos'>('todos');
  categoryFilter = signal<PostCategory | 'todas'>('todas');

  filteredPosts = computed(() => {
  let list = this.posts().filter((p) => p.status === 'approved');

  if (this.categoryFilter() !== 'todas') {
    list = list.filter((p) => p.category === this.categoryFilter());
  }

  if (this.activeTab() === 'amigos') {
    list = list.filter((p) => this.friendService.isFriend(p.authorId));
  }

  return list;
});




  constructor(public authService: AuthService,  private http: HttpClient ) {}
  




  probarPosts() {
  this.http.get('http://localhost:8080/api/posts').subscribe({
    next: (data) => console.log('Posts:', data),
    error: (err) => console.error('Error posts:', err)
  });
}

probarComentarios() {
  this.http.get('http://localhost:8080/api/comentarios').subscribe({
    next: (data) => console.log('Comentarios:', data),
    error: (err) => console.error('Error comentarios:', err)
  });
}



  testClick() {
  console.log('¡Funciona!');
  alert('click detectado');
}
  login() {
  console.log('Botón clickeado');
  console.log('authService:', this.authService);
  try {
    this.authService.login();
    console.log('login() ejecutado sin excepción');
  } catch (e) {
    console.error('Error en login():', e);
  }
}

 getRoles(): string {
  const roles = this.authService.getRole();
  return Array.isArray(roles) ? roles.join(', ') : 'sin roles';
}

  publish(): void {
    const content = this.newContent().trim();
    if (!content) return;

    this.postService.create(
      { content, category: this.newCategory() },
      { id: 'me', name: 'Tú', avatar: '🦊' }
    );
    this.newContent.set('');
  }

  onLike(id: string): void {
    this.postService.like(id);
  }

  onReport(id: string): void {
    this.postService.report(id);
  }

  onAddComment(event: { postId: string; content: string }): void {
    this.postService.addComment(event.postId, event.content);
  }

  onOpenComments(postId: string): void {
    this.postService.loadComments(postId);
  }
}