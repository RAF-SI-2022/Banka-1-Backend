package org.banka1.userservice.domains.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorisedException extends RuntimeException{

    //Login - user credentials not found in database.
    public UnauthorisedException(String message){ super(message); }


}
