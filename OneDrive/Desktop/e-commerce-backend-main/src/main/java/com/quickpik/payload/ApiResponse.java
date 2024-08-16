package com.quickpik.payload;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@JsonPropertyOrder({"timestamp","status","message"})
public class ApiResponse {
	private String message;
	private int status;
	private long timestamp;
}
