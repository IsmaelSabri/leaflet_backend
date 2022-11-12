package com.maps.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000; // 5 días en milisegundos
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "El Token no se puede verficar";
    public static final String AUTHOR = "Isma";
    public static final String ADMINISTRACION = "Portal para la gestión de los usuarios";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "Necesitas logearte en la página";
    public static final String ACCESS_DENIED_MESSAGE = "No tienes permisos para acceder a este contenido";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    //public static final String[] PUBLIC_URLS = { "/user/login", "/user/register", "/user/image/**", "/home", "/map/list" };
    public static final String[] PUBLIC_URLS = { "**" };

}
