import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AdminPostsService } from '../../../services/admin/admin-posts.service';
import { Post, PostCategory } from '../../../models/post-card/post-card-model';

@Component({
  selector: 'app-admin-posts',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-posts.html',
  styleUrls: ['./admin-posts.css']
})
export class AdminPostsComponent implements OnInit {
  private adminPostsService = inject(AdminPostsService);

  posts = signal<Post[]>([]);
  isLoading = signal<boolean>(true);

  // Estados del modal
  isModalOpen = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  currentEditId = signal<string | null>(null);

  // Formulario reactivo
  postForm = new FormGroup({
    category: new FormControl<PostCategory>('General', Validators.required),
    content: new FormControl('', [Validators.required, Validators.minLength(5)])
  });

  ngOnInit() {
    this.cargarPosts();
  }

  cargarPosts() {
    this.isLoading.set(true);
    this.adminPostsService.getAllPosts().subscribe({
      next: (data) => {
        this.posts.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar', err);
        this.isLoading.set(false);
      }
    });
  }

  eliminarPost(id: string) {
    if (confirm('¿Seguro que quieres borrar esto?')) {
      this.adminPostsService.deletePost(id as any).subscribe({
        next: () => this.posts.update(p => p.filter(post => post.id !== id)),
        error: (err) => console.error('Error al eliminar', err)
      });
    }
  }

  toggleEstado(post: Post) {
    if (post.status === 'hidden') {
      this.adminPostsService.quitarRestriccion(Number(post.id)).subscribe({
        next: () => this.posts.update((p) => p.map((x) => (x.id === post.id ? { ...x, status: 'approved' } : x))),
        error: (err) => console.error('Error al aprobar', err),
      });
      return;
    }

    const motivo = prompt('¿Por qué restringes esta publicación?', 'Contenido inapropiado');
    if (motivo === null) return;

    this.adminPostsService.restringirPost(Number(post.id), Number(post.authorId), motivo).subscribe({
      next: () => this.posts.update((p) => p.map((x) => (x.id === post.id ? { ...x, status: 'hidden' } : x))),
      error: (err) => console.error('Error al restringir', err),
    });
  }

  // --- LÓGICA DEL MODAL ---

  abrirModalCrear() {
    this.isEditing.set(false);
    this.currentEditId.set(null);
    this.postForm.reset({ category: 'General', content: '' });
    this.isModalOpen.set(true);
  }

  abrirModalEditar(post: Post) {
    this.isEditing.set(true);
    this.currentEditId.set(post.id);
    this.postForm.patchValue({
      category: post.category,
      content: post.content
    });
    this.isModalOpen.set(true);
  }

  cerrarModal() {
    this.isModalOpen.set(false);
  }

  guardarPost() {
    if (this.postForm.invalid) return;

    const postData = this.postForm.value;

    if (this.isEditing() && this.currentEditId()) {
      // Editar existente
      this.adminPostsService.updatePost(this.currentEditId() as any, postData).subscribe({
        next: (updatedPost) => {
          this.posts.update(current => 
            current.map(p => p.id === updatedPost.id ? updatedPost : p)
          );
          this.cerrarModal();
        },
        error: (err) => console.error('Error al actualizar', err)
      });
    } else {
      // Crear nuevo
      this.adminPostsService.createPost(postData).subscribe({
        next: (newPost) => {
          this.posts.update(current => [newPost, ...current]);
          this.cerrarModal();
        },
        error: (err) => console.error('Error al crear', err)
      });
    }
  }
}
