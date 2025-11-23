package be.iccbxl.gestionprojets.exception;

/**
 * Exception lancée quand une ressource demandée est introuvable.
 * Utilisée dans toute l'application pour gérer les erreurs 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}