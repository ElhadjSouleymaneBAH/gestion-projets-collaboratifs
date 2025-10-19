package be.iccbxl.gestionprojets.controller;

import be.iccbxl.gestionprojets.model.Transaction;
import be.iccbxl.gestionprojets.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur Transactions (alias admin pour le tableau de bord)
 * Aligne le backend avec le frontend : GET /api/transactions/admin/all
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Admin — toutes les transactions
     * URL attendue par le front : /api/transactions/admin/all
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<List<Transaction>> getAllAdmin() {
        return ResponseEntity.ok(transactionService.obtenirToutes());
    }
}
