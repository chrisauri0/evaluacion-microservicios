package com.example.microservicio_restricciones.dto;

public class BanearUsuarioRequest {
    private Long usuarioId;
    private String motivo;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}