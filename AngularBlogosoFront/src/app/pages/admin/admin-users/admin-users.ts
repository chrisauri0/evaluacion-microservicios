import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { AdminUsersService, UsuarioDto, RegistroRequest } from '../../../services/admin/admin-users.service'; 

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-users.html',
  styleUrls: ['./admin-users.css']
})
export class AdminUsersComponent implements OnInit {
  private adminUsersService = inject(AdminUsersService);

  users = signal<UsuarioDto[]>([]);
  isLoading = signal<boolean>(true);

  isModalOpen = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  currentEditId = signal<number | null>(null);

  userForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(3)]),
    nombre: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    rol: new FormControl('ROLE_USER', Validators.required)
  });

  ngOnInit() {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.isLoading.set(true);
    this.adminUsersService.getAllUsers().subscribe({
      next: (data: UsuarioDto[]) => {
        this.users.set(data);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        console.error('Error al cargar usuarios', err);
        this.isLoading.set(false);
      }
    });
  }

  eliminarUsuario(id: number) {
    if (confirm('¿Seguro que quieres borrar a este usuario? Esta acción no se puede deshacer.')) {
      this.adminUsersService.deleteUser(id).subscribe({
        next: () => this.users.update(u => u.filter(user => user.id !== id)),
        error: (err: any) => console.error('Error al eliminar', err)
      });
    }
  }

  abrirModalCrear() {
    this.isEditing.set(false);
    this.currentEditId.set(null);
    this.userForm.reset({ rol: 'ROLE_USER' });
    
    // Al crear, la contraseña SÍ es obligatoria
    this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.userForm.get('password')?.updateValueAndValidity();
    
    this.isModalOpen.set(true);
  }

  abrirModalEditar(user: UsuarioDto) {
    this.isEditing.set(true);
    this.currentEditId.set(user.id);
    this.userForm.patchValue({
      username: user.username,
      email: user.email,
      rol: user.rol,
      nombre: user.nombre
    });

    // Al editar, la contraseña NO es obligatoria (no la vamos a actualizar aquí)
    this.userForm.get('password')?.clearValidators();
    this.userForm.get('password')?.updateValueAndValidity();

    this.isModalOpen.set(true);
  }

  cerrarModal() {
    this.isModalOpen.set(false);
  }

  guardarUsuario() {
    if (this.userForm.invalid) return;

    // Mapeo seguro para que TypeScript no se queje de los nulos
    const userData: RegistroRequest = {
      username: this.userForm.value.username ?? '',
      nombre: this.userForm.value.nombre ?? '',
      email: this.userForm.value.email ?? '',
      password: this.userForm.value.password ?? undefined,
      rol: this.userForm.value.rol ?? 'ROLE_USER'
    };

    if (this.isEditing() && this.currentEditId()) {
      this.adminUsersService.updateUser(this.currentEditId()!, userData).subscribe({
        next: (updatedUser: UsuarioDto) => {
          this.users.update(current => 
            current.map(u => u.id === updatedUser.id ? updatedUser : u)
          );
          this.cerrarModal();
        },
        error: (err: any) => console.error('Error al actualizar', err)
      });
    } else {
      this.adminUsersService.createUser(userData).subscribe({
        next: (newUser: UsuarioDto) => {
          this.users.update(current => [newUser, ...current]);
          this.cerrarModal();
        },
        error: (err: any) => console.error('Error al crear', err)
      });
    }
  }
}