package com.example.backend.controller;

import com.example.backend.dto.request.ExperienceDto;
import com.example.backend.dto.request.FormationDto;
import com.example.backend.dto.response.ExperienceResponseDto;
import com.example.backend.dto.response.FormationResponseDto;
import com.example.backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Profil alumni : expériences et cursus MIAGE d'un utilisateur.
// Le contrôleur ne contient aucune règle métier : il reçoit la requête et passe la main au service.
@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/experiences")
    @ResponseStatus(HttpStatus.CREATED)                       // 201
    public ExperienceResponseDto addExperience(@PathVariable("userId") Integer userId,
                                               @RequestBody ExperienceDto dto) {
        return profileService.addExperience(userId, dto);
    }

    @GetMapping("/experiences")                               // 200
    public List<ExperienceResponseDto> getExperiences(@PathVariable("userId") Integer userId) {
        return profileService.getExperiencesByUser(userId);
    }

    @PostMapping("/formations")
    @ResponseStatus(HttpStatus.CREATED)                       // 201
    public FormationResponseDto addFormation(@PathVariable("userId") Integer userId,
                                             @RequestBody FormationDto dto) {
        return profileService.addFormation(userId, dto);
    }

    @GetMapping("/formations")                                // 200
    public List<FormationResponseDto> getFormations(@PathVariable("userId") Integer userId) {
        return profileService.getFormationsByUser(userId);
    }
}
