package de.format.salzzy.Rechnungsmanager.repository;

import de.format.salzzy.Rechnungsmanager.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}