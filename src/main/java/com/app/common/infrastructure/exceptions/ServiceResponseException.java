/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.app.common.infrastructure.exceptions;

/**
 *
 * @author inuHa
 */
public class ServiceResponseException extends RuntimeException {
    
    public ServiceResponseException(String message) {
	super(message);
    }
    
}
