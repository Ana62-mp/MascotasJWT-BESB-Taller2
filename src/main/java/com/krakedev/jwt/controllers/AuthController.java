package com.krakedev.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioRegistrado = usuarioService.registrar(usuario);

			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar el usuario: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioEncontrado = usuarioService.login(usuario.getUsername(), usuario.getPassword());

			if (usuarioEncontrado != null) {
				return ResponseEntity.ok(usuarioEncontrado);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al iniciar sesión: " + e.getMessage());
		}
	}
}