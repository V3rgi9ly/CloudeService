package com.example.springexample.cloudeservice.service;

import com.example.springexample.cloudeservice.*;
import com.example.springexample.cloudeservice.model.Users;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.example.springexample.cloudeservice.AuthServiceGrpc;


@RequiredArgsConstructor
@GrpcService
public class AuthServiceServer extends AuthServiceGrpc.AuthServiceImplBase {
    private static Log log= LogFactory.getLog(AuthServiceServer.class);
    private final UsersService usersService;

    @Override
    public void getRegistre(RequestRegistre req, StreamObserver<RegisterResponse> streamObserver) {
        Users users = new Users();

        users.setPassword(req.getPassword());
        users.setUsername(req.getLogin());
        usersService.save(users);
        RegisterResponse response = RegisterResponse.newBuilder().setMessage(req.getLogin() + req.getPassword()).build();
        streamObserver.onNext(response);
        streamObserver.onCompleted();

    }

//    @Override
//    public void getLogin(RequestLogin rl, StreamObserver<LoginResponse> streamObserver) {
//        Users users = usersService.findByLogin(rl.getLogin());
//        if (users == null) {
//            streamObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
//
//        }
//        else {
//            LoginResponse loginResponse = LoginResponse.newBuilder().setToken(rl.getLogin() + rl.getPassword()).build();
//            streamObserver.onNext(loginResponse);
//            streamObserver.onCompleted();
//        }
//
//    }
}

