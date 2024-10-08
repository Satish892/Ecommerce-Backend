package com.quickpik.dtos;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.quickpik.entities.AuthenticationProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
	
	
	private String userId;

	@Size(max = 100, message = "First name cant be longer than 100 characters")
	@NotBlank(message = "Please provide a first name")
	private String fname;

	@Size(max = 100, message = "Last name cant be longer than 100 characters")
	@NotBlank(message = "Please provide a last name")
	private String lname;

	@Email(message = "Please provide a valid email")
	@NotBlank(message = "Please provide an email")
	private String email;

	@Size(min = 8, message = "Password must be at least 8 characters")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\\s).{8,15}$", message = "Password should contain atleast one uppercase, one lowercase, one number and one special character")
	private String password;

	@Size(min = 10, max = 10, message = "Please provide a valid 10 digit phone number")
	private String phone;

	private String address;


	@Pattern(regexp = "^(?!.*[DFIOQUdfioqu])[A-VXYa-vxy][0-9][A-Za-z] ?[0-9][A-Za-z][0-9]?|\\s*$", message = "Please enter a valid postal code")
	private String postalCode;

	@Size(max = 150, message = "City name cant be longer than 150 characters")
	private String city;
	@Size(max = 100, message = "Province name cant be longer than 100 characters")
	private String province;

	// Custom validator annotation
//	@ImageNameValid
	private String image;
	
	private AuthenticationProvider authenticationProvider;
	
	@Builder.Default
	private Set<RoleDto> roles = new HashSet<>();

	private Date createdAt;
	private Date updatedAt;
}