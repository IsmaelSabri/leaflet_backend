package com.maps.service.impl;

import com.maps.constant.*;
import com.maps.dao.IUsuarioRepository;
import com.maps.entities.*;
import com.maps.enums.Rol;
import com.maps.exception.*;
import com.maps.service.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.maps.constant.FileConstant.*;
import static com.maps.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static com.maps.enums.Rol.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.*;
//@Component
//@Configuration
//@ConfigurationProperties("storage")
//@ConstructorBinding
@Service
@Transactional
@Qualifier("userDetailsService")
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	private IUsuarioRepository usuarioRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;
	
	public UsuarioServiceImpl() {
		
	}

	@Autowired
    public UsuarioServiceImpl(IUsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findUsuarioByUsername(username);
		if (usuario == null) {
			LOGGER.error(UsuarioConstant.NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(UsuarioConstant.NO_USER_FOUND_BY_USERNAME + username);
		} else {
			validateLoginAttempt(usuario);
			usuario.setMostrarFechaDeUltimoAcceso(usuario.getMostrarFechaDeUltimoAcceso());
			usuario.setMostrarFechaDeUltimoAcceso(new Date());
			usuarioRepository.save(usuario);
			UsuarioPrincipal usuarioPrincipal = new UsuarioPrincipal(usuario);
			LOGGER.info(UsuarioConstant.FOUND_USER_BY_USERNAME + username);
			return usuarioPrincipal;
		}
	}

	@Override
	public Usuario register(String firstName, String lastName, String username, String email)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		Usuario usuario = new Usuario();
		usuario.setUsuarioId(generateUserId());
		String password = generatePassword();
		usuario.setNombre(firstName);
		usuario.setPrimerApellido(lastName);
		usuario.setUsername(username);
		usuario.setEmail(email);
		usuario.setFechaRegistro(new Date());
		usuario.setPassword(encodePassword(password));
		usuario.setActive(true);
		usuario.setNotLocked(true);
		usuario.setRol(ROLE_USER.name());
		usuario.setAuthorities(ROLE_USER.getAuthorities());
		usuario.setFotoPerfilUrl(getTemporaryProfileImageUrl(username));
		usuarioRepository.save(usuario);
		LOGGER.info("New usuario password: " + password);
		emailService.sendNewPasswordEmail(firstName, password, email);
		return usuario;
	}

	@Override
	public Usuario addNewUsuario(String firstName, String lastName, String username, String email, String role,
			boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		validateNewUsernameAndEmail(EMPTY, username, email);
		Usuario usuario = new Usuario();
		String password = generatePassword();
		usuario.setUsuarioId(generateUserId());
		usuario.setNombre(firstName);
		usuario.setPrimerApellido(lastName);
		usuario.setFechaRegistro(new Date());
		usuario.setUsername(username);
		usuario.setEmail(email);
		usuario.setPassword(encodePassword(password));
		usuario.setActive(isActive);
		usuario.setNotLocked(isNonLocked);
		usuario.setRol(getRoleEnumName(role).name());
		usuario.setAuthorities(getRoleEnumName(role).getAuthorities());
		usuario.setFotoPerfilUrl(getTemporaryProfileImageUrl(username));
		usuarioRepository.save(usuario);
		saveProfileImage(usuario, profileImage);
		LOGGER.info("New usuario password: " + password);
		return usuario;
	}

	@Override
	public Usuario updateUsuario(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,
			NotAnImageFileException {
		Usuario usuarioActual = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
		usuarioActual.setNombre(newFirstName);
		usuarioActual.setPrimerApellido(newLastName); 
		usuarioActual.setUsername(newUsername);
		usuarioActual.setEmail(newEmail);
		usuarioActual.setActive(isActive);
		usuarioActual.setNotLocked(isNonLocked);
		usuarioActual.setRol(getRoleEnumName(role).name());
		usuarioActual.setAuthorities(getRoleEnumName(role).getAuthorities());
		usuarioRepository.save(usuarioActual);
		saveProfileImage(usuarioActual, profileImage);
		return usuarioActual;
	}

	@Override
	public void resetPassword(String email) throws MessagingException, EmailNotFoundException {
		Usuario usuario = usuarioRepository.findUsuarioByEmail(email);
		if (usuario == null) {
			throw new EmailNotFoundException(UsuarioConstant.NO_USER_FOUND_BY_EMAIL + email);
		}
		String password = generatePassword();
		usuario.setPassword(encodePassword(password));
		usuarioRepository.save(usuario);
		LOGGER.info("New usuario password: " + password);
		emailService.sendNewPasswordEmail(usuario.getPrimerApellido(), password, usuario.getEmail());
	}

	/*public Usuario updateLastView(String username, String lastView){
		Usuario aux=usuarioRepository.findUsuarioByUsername(username);
		aux.setLastView(lastView);
		usuarioRepository.save(aux);
		return aux;
	}*/

	@Override
	public Usuario updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		Usuario usuario = validateNewUsernameAndEmail(username, null, null);
		saveProfileImage(usuario, profileImage);
		return usuario;
	}

	@Override
	public List<Usuario> getUsuarios() {
		return usuarioRepository.findAll();
	}

	@Override
	public Usuario findUserByUsername(String username) {
		return usuarioRepository.findUsuarioByUsername(username);
	}

	@Override
	public Usuario findUsuarioByEmail(String email) {
		return usuarioRepository.findUsuarioByEmail(email);
	}

	@Override
	public void deleteUsuario(String nombre) throws IOException {
		Usuario usuario = usuarioRepository.findUsuarioByUsername(nombre);
		Path usuarioFolder = Paths.get(USER_FOLDER + usuario.getUsername()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(usuarioFolder.toString()));
		usuarioRepository.deleteById(usuario.getId());
	}

	private void saveProfileImage(Usuario usuario, MultipartFile profileImage) throws IOException, NotAnImageFileException {
		if (profileImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path usuarioFolder = Paths.get(USER_FOLDER + usuario.getUsername()).toAbsolutePath().normalize();
			if (!Files.exists(usuarioFolder)) {
				Files.createDirectories(usuarioFolder);
				LOGGER.info(DIRECTORY_CREATED + usuarioFolder);
			}
			Files.deleteIfExists(Paths.get(usuarioFolder + usuario.getUsername() + DOT + JPG_EXTENSION));
			Files.copy(profileImage.getInputStream(), usuarioFolder.resolve(usuario.getUsername() + DOT + JPG_EXTENSION),
					REPLACE_EXISTING);
			usuario.setFotoPerfilUrl(setProfileImageUrl(usuario.getUsername()));
			usuarioRepository.save(usuario);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
		}
	}

	private String setProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
	}

	private Rol getRoleEnumName(String role) {
		return Rol.valueOf(role.toUpperCase());
	}

	private String getTemporaryProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username)
				.toUriString();
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private void validateLoginAttempt(Usuario usuario) {
		if (usuario.isNotLocked()) {
			if (loginAttemptService.hasExceededMaxAttempts(usuario.getUsername())) {
				usuario.setNotLocked(false);
			} else {
				usuario.setNotLocked(true);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(usuario.getUsername());
		}
	}

	private Usuario validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UserNotFoundException, UsernameExistException, EmailExistException {
		Usuario usuarioByNewUsername = findUserByUsername(newUsername);
		Usuario usuarioByNewEmail = findUsuarioByEmail(newEmail);
		if (StringUtils.isNotBlank(currentUsername)) {
			Usuario usuarioActual = findUserByUsername(currentUsername);
			if (usuarioActual == null) {
				throw new UserNotFoundException(UsuarioConstant.NO_USER_FOUND_BY_USERNAME + currentUsername + "J");
			}
			if (usuarioByNewUsername != null && !usuarioActual.getId().equals(usuarioByNewUsername.getId())) {
				throw new UsernameExistException(UsuarioConstant.USERNAME_ALREADY_EXISTS);
			}
			if (usuarioByNewEmail != null && !usuarioActual.getId().equals(usuarioByNewEmail.getId())) {
				throw new EmailExistException(UsuarioConstant.EMAIL_ALREADY_EXISTS);
			}
			return usuarioActual;
		} else {
			if (usuarioByNewUsername != null) {
				throw new UsernameExistException(UsuarioConstant.USERNAME_ALREADY_EXISTS);
			}
			if (usuarioByNewEmail != null) {
				throw new EmailExistException(UsuarioConstant.EMAIL_ALREADY_EXISTS);
			}
			return null;
		}
	}


}
