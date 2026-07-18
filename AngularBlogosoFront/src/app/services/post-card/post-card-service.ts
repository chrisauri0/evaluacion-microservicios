import { Injectable, signal } from '@angular/core';
import { Post, NewPost, Comment } from '../../models/post-card/post-card-model';

const MOCK_POSTS: Post[] = [
  { id: '1', authorId: 'u1', authorName: 'Ana Ríos', authorAvatar: '🐼', createdAt: new Date(), category: 'Noticias', content: '¡Bienvenidos al nuevo tablón! Compartan con respeto 🙂', status: 'approved', likes: 12, reports: 0, comments: [] },
  { id: '2', authorId: 'u2', authorName: 'Carlos Lima', authorAvatar: '🦁', createdAt: new Date(), category: 'Humor', content: '¿Alguien más piensa que los lunes deberían ser opcionales?', status: 'approved', likes: 8, reports: 0, comments: [] },
];

@Injectable({ providedIn: 'root' })
export class PostService {
  private readonly _posts = signal<Post[]>(MOCK_POSTS);
  posts = this._posts.asReadonly();

  create(newPost: NewPost, author: { id: string; name: string; avatar: string }): void {
    const post: Post = {
      id: crypto.randomUUID(),
      authorId: author.id,
      authorName: author.name,
      authorAvatar: author.avatar,
      createdAt: new Date(),
      category: newPost.category,
      content: newPost.content,
      status: 'approved',
      likes: 0,
      reports: 0,
      comments: [],
    };
    this._posts.update((posts) => [post, ...posts]);
  }

  like(id: string): void {
    this._posts.update((posts) => posts.map((p) => (p.id === id ? { ...p, likes: p.likes + 1 } : p)));
  }

  report(id: string): void {
    this._posts.update((posts) => posts.map((p) => (p.id === id ? { ...p, reports: p.reports + 1 } : p)));
  }

  addComment(postId: string, content: string, author: { name: string; avatar: string }): void {
    const comment: Comment = {
      id: crypto.randomUUID(),
      postId,
      authorName: author.name,
      authorAvatar: author.avatar,
      content,
      createdAt: new Date(),
    };
    this._posts.update((posts) =>
      posts.map((p) => (p.id === postId ? { ...p, comments: [...p.comments, comment] } : p))
    );
  }

  delete(id: string): void {
  this._posts.update((posts) => posts.filter((p) => p.id !== id));
}
}