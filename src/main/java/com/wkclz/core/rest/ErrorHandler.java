package com.wkclz.core.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorHandler {

    @GetMapping(value = "/error")
    public ResponseEntity<ErrorBean> error(HttpServletRequest request) {
        String message = request.getAttribute("javax.servlet.error.message").toString();
        ErrorBean errorBean = new ErrorBean();
        errorBean.setMessage(message);
        errorBean.setReason("程序出错");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_GATEWAY);
    }

    private static class ErrorBean {
        private String message;

        private String reason;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}