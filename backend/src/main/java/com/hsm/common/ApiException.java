package com.hsm.common;
public class ApiException extends RuntimeException { private final int status; private final String code; public ApiException(int s,String c,String m){super(m);status=s;code=c;} public int getStatus(){return status;} public String getCode(){return code;} }
