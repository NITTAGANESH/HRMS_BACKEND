package com.SPYDTECH.HRMS.entites;//package com.Eonline.Education.modals;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//
//@Getter
//public class OtpVerificationRequest {
//
//    @NotBlank(message = "OTP is required")
//    @Size(min = 6, max = 6, message = "OTP must be 6 characters long")
//    private String otp;
//
//    public OtpVerificationRequest(String otp) {
//        this.otp = otp;
//    }
//
//    // No setter for OTP to maintain immutability
//
//    /**
//     * Creates an {@code OtpVerificationRequest} instance with the specified OTP.
//     *
//     * @param otp the OTP to be verified
//     * @return an {@code OtpVerificationRequest} instance
//     */
//    public static OtpVerificationRequest of(String otp) {
//        return new OtpVerificationRequest(otp);
//    }
//
//    /**
//     * Returns a string representation of the {@code OtpVerificationRequest}.
//     *
//     * @return a string representation of the object
//     */
//    @Override
//    public String toString() {
//        return "OtpVerificationRequest{" +
//                "otp='" + otp + '\'' +
//                '}';
//    }
//}


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class OtpVerificationRequest {
    @Id
    private String otp;

    // Getters and setters
}