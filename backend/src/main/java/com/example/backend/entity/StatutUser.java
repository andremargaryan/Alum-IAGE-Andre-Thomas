package com.example.backend.entity;

// Mêmes valeurs que l'ENUM users.statut en base.
// SUPPRIME = suppression logique : on ne supprime jamais un user de la base.
public enum StatutUser {
    ACTIF,
    SUSPENDU,
    SUPPRIME
}
