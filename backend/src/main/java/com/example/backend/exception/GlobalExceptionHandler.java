package com.example.backend.exception;

import com.example.backend.dto.response.ErreurDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Gestion centralisée des erreurs : toute exception qui sort d'un contrôleur, d'un service
// ou de JwtInterceptor arrive ici et devient une réponse JSON au même format (ErreurDto).
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Erreurs levées volontairement dans le code : 400, 401, 403, 404, 409...
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErreurDto> erreurMetier(ResponseStatusException e, HttpServletRequest requete) {
        HttpStatus statut = HttpStatus.valueOf(e.getStatusCode().value());
        String message = (e.getReason() != null) ? e.getReason() : statut.getReasonPhrase();
        return reponse(statut, message, requete, null);
    }

    // @Valid a refusé le JSON : on renvoie la liste des champs fautifs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDto> validation(MethodArgumentNotValidException e, HttpServletRequest requete) {
        Map<String, String> champs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(erreur -> champs.put(erreur.getField(), erreur.getDefaultMessage()));
        return reponse(HttpStatus.BAD_REQUEST, "Données invalides", requete, champs);
    }

    // JSON mal formé, ou mauvais type (du texte à la place d'un nombre...)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurDto> jsonIllisible(HttpMessageNotReadableException e, HttpServletRequest requete) {
        return reponse(HttpStatus.BAD_REQUEST, "Corps de la requête illisible : vérifiez le format JSON", requete, null);
    }

    // Paramètre ou en-tête manquant, ou de mauvais type (ex : /users/abc au lieu de /users/3)
    @ExceptionHandler({MissingServletRequestParameterException.class, MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErreurDto> parametre(Exception e, HttpServletRequest requete) {
        return reponse(HttpStatus.BAD_REQUEST, "Paramètre manquant ou invalide : " + e.getMessage(), requete, null);
    }

    // La base a refusé l'opération (doublon, donnée encore utilisée ailleurs...)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErreurDto> contrainteBase(DataIntegrityViolationException e, HttpServletRequest requete) {
        log.warn("Contrainte de la base violée sur {} : {}", requete.getRequestURI(), e.getMostSpecificCause().getMessage());
        return reponse(HttpStatus.CONFLICT, "Opération refusée par la base de données (doublon ou donnée liée)", requete, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErreurDto> routeInconnue(NoResourceFoundException e, HttpServletRequest requete) {
        return reponse(HttpStatus.NOT_FOUND, "Route inconnue", requete, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErreurDto> methode(HttpRequestMethodNotSupportedException e, HttpServletRequest requete) {
        return reponse(HttpStatus.METHOD_NOT_ALLOWED, "Méthode " + e.getMethod() + " non autorisée sur cette route", requete, null);
    }

    // Filet de sécurité : tout le reste. On journalise le détail, mais on ne l'envoie jamais au client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurDto> inattendue(Exception e, HttpServletRequest requete) {
        log.error("Erreur inattendue sur {} {}", requete.getMethod(), requete.getRequestURI(), e);
        return reponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur", requete, null);
    }

    private ResponseEntity<ErreurDto> reponse(HttpStatus statut, String message, HttpServletRequest requete,
                                              Map<String, String> details) {
        ErreurDto corps = new ErreurDto(LocalDateTime.now(), statut.value(), statut.getReasonPhrase(),
                message, requete.getRequestURI(), details);
        return ResponseEntity.status(statut).body(corps);
    }
}
