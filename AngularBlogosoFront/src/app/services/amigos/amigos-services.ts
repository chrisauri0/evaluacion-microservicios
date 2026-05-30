import { Injectable, signal, computed } from '@angular/core';
import { User } from '../../models/amigos/amigos-model';

const ALL_USERS: User[] = [
  { id: 'u1', name: 'Ana Ríos', avatar: '🐼' },
  { id: 'u2', name: 'Carlos Lima', avatar: '🦁' },
  { id: 'u3', name: 'Marta Ibáñez', avatar: '🐹' },
  { id: 'u4', name: 'Diego Soto', avatar: '🐸' },
];

@Injectable({ providedIn: 'root' })
export class FriendService {
  private readonly _friendIds = signal<Set<string>>(new Set(['u2']));

  allUsers = signal<User[]>(ALL_USERS);

  friends = computed(() =>
    this.allUsers().filter((u) => this._friendIds().has(u.id))
  );

  others = computed(() =>
    this.allUsers().filter((u) => !this._friendIds().has(u.id))
  );

  isFriend(userId: string): boolean {
    return this._friendIds().has(userId);
  }

  add(userId: string): void {
    this._friendIds.update((ids) => new Set(ids).add(userId));
  }

  remove(userId: string): void {
    this._friendIds.update((ids) => {
      const next = new Set(ids);
      next.delete(userId);
      return next;
    });
  }
}