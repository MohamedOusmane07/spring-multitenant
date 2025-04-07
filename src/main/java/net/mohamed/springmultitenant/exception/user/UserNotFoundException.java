package net.mohamed.springmultitenant.exception.user;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {super(message);}
}
