export type PostCategory = 'Anuncios' | 'General' | 'Preguntas' | 'Humor' | 'Noticias';
export type PostStatus = 'approved' | 'pending' | 'hidden';

export interface Comment {
  id: string;
  postId: string;
  authorName: string;
  authorAvatar: string;
  content: string;
  createdAt: Date;
}

export interface Post {
  id: string;
  authorId: string;
  authorName: string;
  authorAvatar: string;
  createdAt: Date;
  category: PostCategory;
  content: string;
  status: PostStatus;
  likes: number;
  reports: number;
  comments: Comment[];
}

export interface NewPost {
  content: string;
  category: PostCategory;
}