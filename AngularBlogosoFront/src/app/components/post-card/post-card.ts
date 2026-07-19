import { Component, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Post } from '../../models/post-card/post-card-model';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './post-card.html',
})
export class PostCardComponent {
  post = input.required<Post>();

  like = output<string>();
  report = output<string>();
  addComment = output<{ postId: string; content: string }>();
  openComments = output<string>();

  showComments = signal(false);
  newComment = signal('');

  toggleComments(): void {
    const willOpen = !this.showComments();
    this.showComments.set(willOpen);
    if (willOpen) {
      this.openComments.emit(this.post().id);
    }
  }

  submitComment(): void {
    const content = this.newComment().trim();
    if (!content) return;

    this.addComment.emit({ postId: this.post().id, content });
    this.newComment.set('');
  }
}