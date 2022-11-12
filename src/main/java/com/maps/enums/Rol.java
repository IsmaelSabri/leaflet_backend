package com.maps.enums;

import static com.maps.constant.Authority.*;

public enum Rol {
	ROLE_USER(USER_AUTHORITIES), ROLE_HR(HR_AUTHORITIES), ROLE_MANAGER(MANAGER_AUTHORITIES),
	ROLE_ADMIN(ADMIN_AUTHORITIES), ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

	private String[] authorities;

	Rol(String... authorities) {
		this.authorities = authorities;
	}

	public String[] getAuthorities() {
		return authorities;
	}
}
