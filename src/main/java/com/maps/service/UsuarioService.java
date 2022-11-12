package com.maps.service;

import com.maps.entities.Usuario;
import com.maps.exception.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UsuarioService {

	Usuario register(String firstName, String lastName, String username, String email)
			throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

	List<Usuario> getUsuarios();

	Usuario findUserByUsername(String nombre);

	Usuario findUsuarioByEmail(String email);

	Usuario addNewUsuario(String firstName, String lastName, String username, String email, String role,
			boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

	Usuario updateUsuario(String currentUsuarioname, String newFirstName, String newLastName, String newUsuarioname,
			String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws UserNotFoundException, UsernameExistException, EmailExistException, IOException,
			NotAnImageFileException;

	void deleteUsuario(String username) throws IOException;

	void resetPassword(String email) throws MessagingException, EmailNotFoundException;

	Usuario updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException,
			UsernameExistException, EmailExistException, IOException, NotAnImageFileException;
}
