package com.msmk.PocketPulse.service;

import com.msmk.PocketPulse.dto.AuthDTO;
import com.msmk.PocketPulse.dto.ProfileDTO;
import com.msmk.PocketPulse.entity.ProfileEntity;
import com.msmk.PocketPulse.repository.ProfileRepository;
import com.msmk.PocketPulse.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setIsActive(true); // Auto-activate account
        newProfile = profileRepository.save(newProfile);
        return toDTO(newProfile);
    }


//    public ProfileDTO registerProfile(ProfileDTO profileDTO){
//        ProfileEntity newProfile = toEntity(profileDTO);
//        newProfile.setActivationToken(UUID.randomUUID().toString());
//        newProfile = profileRepository.save(newProfile);
//        String activationLink = activationURL+"/api/v1.0/activate?token="+ newProfile.getActivationToken();
//        String subject  = "Activate your Pocket Pluse account";
//        String body = "Click on the following link to activate your acccount: "+activationLink;
//        emailService.sendEmail(newProfile.getEmail(), subject, body);
//        return toDTO(newProfile);
//    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    // Remove email activation methods
    public boolean isAccountActive(String email) {
        return true; // All accounts are active
    }

//    public boolean isAccountActive(String email){
//        return profileRepository.findByEmail(email)
//                .map(ProfileEntity::getIsActive)
//                .orElse(false);
//    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName()).orElseThrow( () -> new UsernameNotFoundException("Profile not found with email: "+ authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser;
        if (email == null) {
            currentUser = getCurrentProfile(); // Throws exception if not found
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }
        return toDTO(currentUser);
    }

    public Map<String, Object> authenticatedAndGenerateToken(AuthDTO authDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
            );

            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDTO.getEmail())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

//    public Map<String, Object> authenticatedAndGenerateToken(AuthDTO authDTO) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword())
//            );
//
//            String token = jwtUtil.generateToken(authDTO.getEmail());
//            return Map.of(
//                    "token", token,
//                    "user", getPublicProfile(authDTO.getEmail())
//            );
//        } catch (BadCredentialsException e) {
//            // This is thrown when username/password is wrong
//            throw new RuntimeException("Invalid email or password");
//        } catch (DisabledException e) {
//            // This is thrown when user is not active
//            throw new RuntimeException("Account is not active");
//        } catch (Exception e) {
//            // Log the actual exception for debugging
//            e.printStackTrace();
//            throw new RuntimeException("Authentication failed: " + e.getMessage());
//        }
//    }

}
