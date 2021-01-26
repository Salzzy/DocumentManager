package de.format.salzzy.Rechnungsmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.format.salzzy.Rechnungsmanager.model.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

}
