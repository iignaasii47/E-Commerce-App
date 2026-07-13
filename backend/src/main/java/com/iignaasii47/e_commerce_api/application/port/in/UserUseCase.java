package com.iignaasii47.e_commerce_api.application.port.in;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;

public interface UserUseCase {

    User register(User user);

    Authentication login(String email, String password);

}
