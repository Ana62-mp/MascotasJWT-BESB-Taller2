package com.krakedev.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.services.UsuarioService;
import com.krakedev.jwt.utils.JwtUtil;

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
				String token = JwtUtil.generarToken(usuarioEncontrado.getUsername(), usuarioEncontrado.getRol());
				
				return ResponseEntity.ok("{\"token\":\""+ token+"\"}");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al iniciar sesión: " + e.getMessage());
		}
	}
	
	
	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(@RequestHeader("Authorization") String authHeader){
		try {
			if(authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no enviado, formato incorrecto");
			}
			
			String token = authHeader.replace("Bearer ", "");
			
			DecodedJWT jwt = JwtUtil.validarToken(token);
			String username = jwt.getSubject();
			String rol = jwt.getClaim("rol").asString();
			
			if("ADMIN".equals(rol)) {
				return ResponseEntity.ok("Bienvenido adoptante del refugio " +username +". Tienes acceso.");
			}else if("USER".equals(rol)) {
				return ResponseEntity.ok("Bienvenido adoptante del refugio " +username +". Tienes acceso.");
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol no autorizado");
			}
			
			
		} catch(Exception e){
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado.");
		}
	}
	
	
}