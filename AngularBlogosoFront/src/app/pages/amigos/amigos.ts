import { Component, inject } from '@angular/core';
import { FriendService } from '../../services/amigos/amigos-services';

@Component({
  selector: 'app-amigos',
  standalone: true,
  templateUrl: './amigos.html',
})
export class AmigosComponent {
  friendService = inject(FriendService);

  onAdd(userId: string): void {
    this.friendService.add(userId);
  }

  onRemove(userId: string): void {
    this.friendService.remove(userId);
  }
}