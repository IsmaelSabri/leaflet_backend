package com.maps.controller;

import com.maps.entities.HttpResponse;
import com.maps.entities.Usuario;
import com.maps.entities.UsuarioPrincipal;
import com.maps.exception.*;
import com.maps.jwt.JWTTokenProvider;
import com.maps.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.maps.constant.FileConstant.*;
import static com.maps.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = { "/", "/user" })
@CrossOrigin(origins = { "*" }) // revisar esto
public class UsuarioController extends ManejoExcepciones {

	public static final String EMAIL_SENT = "Email enviado con el nuevo password a: ";
	public static final String USER_DELETED_SUCCESSFULLY = "Usuario eliminado correctamente";
	private AuthenticationManager authenticationManager;
	@Autowired
	private UsuarioService usuarioService;
	private JWTTokenProvider jwtTokenProvider;

	@Autowired
	    public void UserResource(AuthenticationManager authenticationManager, UsuarioService usuarioService, JWTTokenProvider jwtTokenProvider) {
	        this.authenticationManager = authenticationManager;
	        this.usuarioService = usuarioService;
	        this.jwtTokenProvider = jwtTokenProvider;
	    }

	@PostMapping("/login")
	public ResponseEntity<Usuario> login(@RequestBody Usuario usuario) {
		authenticate(usuario.getUsername(), usuario.getPassword());
		Usuario loginUser = usuarioService.findUserByUsername(usuario.getUsername());
		UsuarioPrincipal usuarioPrincipal = new UsuarioPrincipal(loginUser);
		HttpHeaders jwtHeader = getJwtHeader(usuarioPrincipal);
		return new ResponseEntity<>(loginUser, jwtHeader, OK);
	}

	@PostMapping("/register")
	public ResponseEntity<Usuario> register(@RequestBody Usuario usuario)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		Usuario nuevoUsuario = usuarioService.register(usuario.getNombre(), usuario.getPrimerApellido(), usuario.getUsername(),
				usuario.getEmail());
		return new ResponseEntity<>(nuevoUsuario, OK);
	}

	@PostMapping("/add")
	public ResponseEntity<Usuario> addNewUser(@RequestParam("nombre") String nombre,
			@RequestParam("primerApellido") String primerApellido, @RequestParam("username") String username,
			@RequestParam("email") String email, @RequestParam("role") String role,
			@RequestParam("isActive") String isActive, @RequestParam("isNonLocked") String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,
			NotAnImageFileException {
		Usuario newUser = usuarioService.addNewUsuario(nombre, primerApellido, username, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		return new ResponseEntity<>(newUser, OK);
	}

	@PostMapping("/update")
	public ResponseEntity<Usuario> update(@RequestParam("currentUsername") String currentUsername,
			@RequestParam("nombre") String nombre, @RequestParam("primerApellido") String primerApellido,
			@RequestParam("username") String username, @RequestParam("email") String email,
			@RequestParam("rol") String role, @RequestParam("isActive") String isActive,
			@RequestParam("isNonLocked") String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile fotoPerfilUrl)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,
			NotAnImageFileException {
		Usuario updatedUser = usuarioService.updateUsuario(currentUsername, nombre, primerApellido, username, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), fotoPerfilUrl);
		return new ResponseEntity<>(updatedUser, OK);
	}

	@GetMapping("/find/{username}")
	public ResponseEntity<Usuario> getUser(@PathVariable("user") String Username) {
		Usuario usuario = usuarioService.findUserByUsername(Username);
		return new ResponseEntity<>(usuario, OK);
	}

	@GetMapping("/list")
	public ResponseEntity<List<Usuario>> getAllUsers() {
		List<Usuario> usuarios = usuarioService.getUsuarios();
		return new ResponseEntity<>(usuarios, OK);
	}

	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email)
			throws MessagingException, EmailNotFoundException {
		usuarioService.resetPassword(email);
		return response(OK, EMAIL_SENT + email);
	}

	@DeleteMapping("/delete/{username}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
		usuarioService.deleteUsuario(username);
		return response(OK, USER_DELETED_SUCCESSFULLY);
	}

	@PostMapping("/updateProfileImage")
	public ResponseEntity<Usuario> updateProfileImage(@RequestParam("username") String Username,
			@RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		Usuario usuario = usuarioService.updateProfileImage(Username, profileImage);
		return new ResponseEntity<>(usuario, OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}

	@GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (InputStream inputStream = url.openStream()) {
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk, 0, bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(
				new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message),
				httpStatus);
	}

	private HttpHeaders getJwtHeader(UsuarioPrincipal user) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(user));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

}
