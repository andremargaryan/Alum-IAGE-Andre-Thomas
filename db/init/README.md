Les fichiers .sql de ce dossier sont exécutés par MySQL au premier démarrage (volume vide), par ordre alphabétique :
01_schema.sql (structure) puis 02_seed.sql (données de test).
Pour les rejouer : docker compose down -v  puis  docker compose up -d
Toute modif de structure : modifier 01_schema.sql (ou exporter depuis phpMyAdmin), commit, puis chacun fait down -v / up -d.
