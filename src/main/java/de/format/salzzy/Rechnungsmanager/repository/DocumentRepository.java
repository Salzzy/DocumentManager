package de.format.salzzy.Rechnungsmanager.repository;

import de.format.salzzy.Rechnungsmanager.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByStatus(Integer status);

}
