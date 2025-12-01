package se.floremila.checkgo.service;

import se.floremila.checkgo.dto.AuthResponse;
import se.floremila.checkgo.dto.LoginRequest;
import se.floremila.checkgo.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

