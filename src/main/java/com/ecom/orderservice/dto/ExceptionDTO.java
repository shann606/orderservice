package com.ecom.orderservice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionDTO  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5434408517482623224L;
	private String status;
	private String details;

}
