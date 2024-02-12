package com.flipkart.fms.Util;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponseStructure{
	private int status;
	private String message;
}
